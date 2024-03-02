package syncgod.log;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import syncgod.config.Config;
import syncgod.config.ConfigValue;
import syncgod.config.Parameter;
import syncgod.pubsub.EventBus;
import syncgod.pubsub.events.*;

/**
 * LogPane Controller.
 *
 * @author tj18b
 */
public class LogPane implements Initializable {

    private static final Logger LOG = Logger.getLogger(LogPane.class.getName());
    private ObservableList<LogEntry> logEntries;

    @FXML
    private TitledPane logHolder;
    @FXML
    private ListView<LogEntry> logContent;

    private String logPath = "";

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        logEntries = FXCollections.observableArrayList();
        logContent.setItems(logEntries);
    }

    /**
     * Tries to parse a log and find start entry.
     */
    public LogPane() {
        EventBus.getInstance().subscribe(LoadLogEvent.class.toString(), this);
        EventBus.getInstance().subscribe(FrameChangeEvent.class.toString(), this);
        EventBus.getInstance().subscribe(RequestDataEvent.class.toString(), this);
        EventBus.getInstance().subscribe(ClearVideosEvent.class.toString(), this);
    }

    /**
     * Loads a log from given path and tries to remove unnecessary entries and display relative
     * game time instead of absolute time.
     * @param path to the log
     */
    private void loadLog(String path) {
        try {
            logPath = path;
            List<LogEntry> entries = new ArrayList<>(LogParser.parse(logPath));

            Optional<LogEntry> startEntry = entries.stream()
                    .filter(LogPane::isStartEntry)
                    .findFirst();

            if (!startEntry.isPresent()) {
                LOG.log(Level.WARNING,
                        "Startentry with message \"" + Parameter.STARTSTRING + "\" not found");
                return;
            }
            logEntries.clear();
            logEntries.addAll(entries);
            logHolder.setText(Paths.get(path).getFileName().toString().split("\\.")[0]);
            removeEntries(startEntry.get());
            logContent.getSelectionModel().select(startEntry.get());
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Failed to parse logfile", ex);
        }
    }

    /**
     * Tries to remove all entries outside of relevant game entries.
     * The Log can contain 2 or more entries which indicate the start of a half and normally two
     * entries indicating the end of the half. To find the halves this method find the first
     * start entry and the first start entry after the first half.
     * @param startEntry first start entry of log
     */
    private void removeEntries(LogEntry startEntry) {
        int startIndex = logEntries.indexOf(startEntry);
        logEntries.remove(0, startIndex);

        List<LogEntry> halfEndEntries =
                logEntries.stream().filter(LogPane::isEndEntry).collect(Collectors.toList());
        if (halfEndEntries.size() < 2) {
            return;
        }
        int firstHalfEndIndex = logEntries.indexOf(halfEndEntries.get(0));

        Optional<LogEntry> secondStart = logEntries.subList(firstHalfEndIndex, logEntries.size())
            .stream().filter(LogPane::isStartEntry).findFirst();
        if (!secondStart.isPresent()) {
            return;
        }
        int secondStartIndex = logEntries.indexOf(secondStart.get());
        logEntries.remove(firstHalfEndIndex + 1, secondStartIndex);
        int secondHalfEndIndex = logEntries.indexOf(halfEndEntries.get(halfEndEntries.size() - 1));
        logEntries.remove(secondHalfEndIndex + 1, logEntries.size());
        calculateGameTime(startEntry, halfEndEntries.get(0),secondStart.get());
    }

    /**
     * Sets the time to relative time (game time) without the time of the break between the halves.
     * @param firstStartEntry first half start entry
     * @param firstHalfEndEntry first half end entry
     * @param secondStartEntry second half start entry
     */
    private void calculateGameTime(LogEntry firstStartEntry, LogEntry firstHalfEndEntry,
                                   LogEntry secondStartEntry) {
        int firstStartTime = firstStartEntry.getTime().toSecondOfDay();
        int halfDuration = (int) Duration.between(firstHalfEndEntry.getTime(),
                secondStartEntry.getTime()).getSeconds();
        LogEntry entry;

        for (int i = 0; i < logEntries.indexOf(secondStartEntry); i++) {
            entry = logEntries.get(i);
            entry.setTime(entry.getTime().minusSeconds(firstStartTime));
        }
        for (int i = logEntries.indexOf(secondStartEntry); i < logEntries.size(); i++) {
            entry = logEntries.get(i);
            entry.setTime(entry.getTime().minusSeconds(halfDuration + firstStartTime));
        }
    }

    /**
     * Checks if given entry is the start entry.
     *
     * @param entry LogEntry to be checked
     * @return true if equal start string
     */
    private static boolean isStartEntry(final LogEntry entry) {
        return entry.message().equals(Parameter.STARTSTRING);
    }

    /**
     * Checks if given entry is the end entry.
     *
     * @param entry LogEntry to be checked
     * @return true if equal end string
     */
    private static boolean isEndEntry(final LogEntry entry) {
        return entry.message().equals(Parameter.ENDSTRING);
    }

    /**
     * Jumps to frame when an entry exists for given frame.
     *
     * @param frameChangeEvent frames changed
     */
    public void receive(final FrameChangeEvent frameChangeEvent) {
        Platform.runLater(() -> {
            int toSelect = findEntryFromFrame(frameChangeEvent.getFrame());
            if (toSelect < 0) {
                return;
            }
            logContent.getSelectionModel().clearAndSelect(toSelect);
            logContent.scrollTo(logContent.getSelectionModel().getSelectedIndex() - 2);
        });
    }

    /**
     * Handles the load log event.
     *
     * @param loadLogEvent contains path to log file
     */
    public void receive(final LoadLogEvent loadLogEvent) {
        loadLog(loadLogEvent.getPath());
    }

    public void receive(final ClearVideosEvent clearVideosEvent) {
        this.logEntries.clear();
        this.logHolder.setText("Log");
    }

    public void receive(final RequestDataEvent requestDataEvent) {
        ArrayList<String> list = new ArrayList<String>();
        if (!logPath.equals("")) {
            list.add(this.logPath);
        }
        EventBus.getInstance().post(new AnswerLogPathEvent(list));
    }

    /**
     * Binary search for given frame. Searches for entry with given frame in the frame range.
     * @param frame to seek entry for
     * @return entry which contains the frame in it's frame range
     */
    private int findEntryFromFrame(double frame) {
        int low = 0;
        int high = logEntries.size() - 1;
        int mid;
        LogEntry midEntry;

        while (low <= high) {
            mid = (low + high) / 2;
            midEntry = logEntries.get(mid);
            if (getEndFrameFromEntry(midEntry) < frame) {
                low = mid + 1;
            } else if (midEntry.getFrame() > frame) {
                high = mid - 1;
            } else if (midEntry.getFrame() <= frame && frame <= getEndFrameFromEntry(midEntry)) {
                return mid;
            }
        }

        return -1;
    }

    /**
     * Calculates entry frame end range. The only purpose is to manage the LogEntry, the real range
     * cannot be obtained from current log files.
     * @param entry to seek end frame for
     * @return end frame from entry
     */
    private double getEndFrameFromEntry(LogEntry entry) {
        int indexEntry = logEntries.indexOf(entry);
        if (indexEntry >= 0) {
            return logEntries.size() - 1 > indexEntry
                    ? logEntries.get(indexEntry + 1).getFrame() - 1
                    : entry.getFrame() + Config.get(ConfigValue.Fps) - 1;
        }
        return 0;
    }

    /**
     * Calculating frame from selected entry, if duration is negative frame is
     * set to a default value.
     */
    @FXML
    private void onClick() {
        LogEntry selectedEntry = logContent.getSelectionModel().getSelectedItem();
        if (selectedEntry == null) {
            return;
        }
        EventBus.getInstance().post(new SetFrameEvent(selectedEntry.getFrame()));
    }

    @FXML
    private void onDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            loadLog(db.getFiles().get(0).getAbsolutePath());
        }
    }


    @FXML
    private void onDragOver(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        if (event.getGestureSource() != logContent && files.size() == 1) {
            if (files.get(0).getName().matches(".*txt")) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
        }
        event.consume();
    }
}
