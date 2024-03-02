package syncgod.menu;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import syncgod.mltparser.MltData;
import syncgod.mltparser.MltEntry;
import syncgod.mltparser.MltProducer;
import syncgod.mltparser.MltReader;
import syncgod.mltparser.MltTractor;
import syncgod.mltparser.MltWriter;
import syncgod.pubsub.EventBus;
import syncgod.pubsub.events.AnswerLogPathEvent;
import syncgod.pubsub.events.AnswerTrackDataEvent;
import syncgod.pubsub.events.AnswerVideoDataEvent;
import syncgod.pubsub.events.ClearVideosEvent;
import syncgod.pubsub.events.CreateMarkerEvent;
import syncgod.pubsub.events.LoadLogEvent;
import syncgod.pubsub.events.LoadProjectEvent;
import syncgod.pubsub.events.LoadVideoEvent;
import syncgod.pubsub.events.RequestDataEvent;
import syncgod.track.TrackBox;
import syncgod.track.TrackMarker;
import syncgod.util.FxUtil;
import syncgod.videoplayer.VideoPlayer;

/**
 * Menubar Controller.
 *
 * @author tj18b
 */
public class Menu implements Initializable {

    private static final Logger LOG = Logger.getLogger(Menu.class.getName());
    private final ArrayList<String> imageExtensions = new ArrayList<>();
    private final ArrayList<String> logsExtension = new ArrayList<>();
    private final ArrayList<String> videoExtensions = new ArrayList<>();
    @FXML
    private javafx.scene.control.Menu buildMenu;
    @FXML
    private MenuBar menuBar;
    private List<VideoPlayer> player;
    private List<TrackBox> tracks;
    private List<String> log;
    private Boolean saved;

    /**
     * Initializes the Menu Controller.
     *
     * @param url Url
     * @param rb  Resourcebundle
     */
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        videoExtensions.add("*.mp4");
        imageExtensions.add("*.png");
        logsExtension.add("*.txt");
        player = new ArrayList<>();
        tracks = new ArrayList<>();
        log = new ArrayList<>();
        saved = true;

        buildMenu.showingProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                buildMenu.getItems().get(0).fire();
            }
        }));

        EventBus.getInstance().subscribe(AnswerVideoDataEvent.class.toString(), this);
        EventBus.getInstance().subscribe(AnswerTrackDataEvent.class.toString(), this);
        EventBus.getInstance().subscribe(AnswerLogPathEvent.class.toString(), this);
        EventBus.getInstance().subscribe(LoadProjectEvent.class.toString(), this);
    }

    /**
     * Opens new window, where the video file can be chosen and the offset from it as well.
     */
    @FXML
    private void loadVideo() {
        FileChooser chooseVideo = new FileChooser();
        videoExtensions.forEach(x -> chooseVideo.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Video Files", x)));
        Stage stage = (Stage) menuBar.getScene().getWindow();
        List<File> paths = chooseVideo.showOpenMultipleDialog(stage);
        if (paths == null) {
            return;
        }

        FXMLLoader loader = FxUtil.newWindow("/fxml/menu/SelectVideoMenu.fxml", "Enter Offsets",
                                             stage);
        SelectVideoMenu controller = loader.getController();
        controller.setUp(paths.stream().map(File::getAbsolutePath).collect(Collectors.toList()));
    }

    /**
     * Closes the Application.
     */
    @FXML
    private void closeApp() {
        Platform.exit();
        System.exit(0);
    }

    /**
     * Loads image that the user selected. For now just png files are accepted.
     */
    public void loadImage() {
        File chosenFile;
        chosenFile = openFile("Select Image", "Image Files", imageExtensions);
        if (chosenFile != null) {
            EventBus.getInstance().post(new LoadVideoEvent(chosenFile.getAbsolutePath(), 20L));
        }
    }

    /**
     * Loads user selected GCC Log.
     */
    @FXML
    private void loadLog() {
        File chosenFile;
        chosenFile = openFile("Select GCC Log", "Text Files", logsExtension);
        if (chosenFile != null) {
            EventBus.getInstance().post(new LoadLogEvent(chosenFile.getAbsolutePath()));
        }
    }

    private String getOpenPath() {
        EventBus.getInstance().post(new RequestDataEvent());

        if ((player.size() > 0) && !saved) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                                    "Discard unsaved project changes?",
                                    ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.CANCEL) {
                return "";
            }
        }
        EventBus.getInstance().post(new ClearVideosEvent());
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Project");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Project file",
                                                                             "*.mlt");
        chooser.getExtensionFilters().add(filter);
        chooser.setSelectedExtensionFilter(filter);
        File file = chooser.showOpenDialog(menuBar.getScene().getWindow());

        if (file == null) {
            return "";
        } else {
            return file.getPath();
        }

    }

    public void openProject() {
        MltData data = MltReader.read(getOpenPath());
        openProject(data);
    }

    private void openProject(String path) {
        if (!saved) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                                    "Discard unsaved project changes?",
                                    ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.CANCEL) {
                return;
            }
        }
        EventBus.getInstance().post(new ClearVideosEvent());
        MltData data = MltReader.read(path);
        openProject(data);
    }

    /**
     * Opens existing project file.
     */
    private void openProject(MltData data) {
        for (int i = 0; i < data.getProducers().size(); i++) {
            MltProducer producer = data.getProducers().get(i);
            EventBus.getInstance().post(new LoadVideoEvent(producer.getProperty(),
                                                           Long.parseLong(producer.getOffset())));
        }

        MltTractor tractor = data.getTractors().get(0);
        for (int j = 0; j < tractor.getPlaylists().size(); j++) {
            List<MltEntry> entries = tractor.getPlaylists().get(j).getEntries();
            for (MltEntry mltEntry : entries) {
                int index = Integer.parseInt(mltEntry.getProducer().substring(5));
                EventBus.getInstance().post(
                        new CreateMarkerEvent(index,
                                new TrackMarker(Long.parseLong(mltEntry.getIn()),
                                        Long.parseLong(mltEntry.getOut()))));
            }
        }
    }

    /**
     * Saves the current project.
     */
    public void saveProject() {
        String path = getSavePath("Project (*.mlt)", ".mlt");
        if (path.equals("")) {
            return;
        }
        MltData data = new MltData(path, player, tracks);
        EventBus.getInstance().post(new RequestDataEvent());
        MltWriter.writeDocument(data, log, true);
        saved = true;

    }

    private String getSavePath(String fileDescription, String fileExt) {
        EventBus.getInstance().post(new RequestDataEvent());
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Project");

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(fileDescription,
                                                                             "*" + fileExt);
        chooser.getExtensionFilters().add(filter);
        chooser.setSelectedExtensionFilter(filter);
        File file = chooser.showSaveDialog(menuBar.getScene().getWindow());
        if (file != null) {
            if (!file.getName().endsWith(fileExt)) {
                return file.getPath() + fileExt;
            }
        } else {
            return "";
        }

        return file.getPath();
    }

    /**
     * Should help the user somehow. Somehow. WIP.
     */
    public void help() {
        FxUtil.newWindow("/fxml/menu/HelpMenu.fxml", "Help Menu",
                         menuBar.getScene().getWindow());
    }

    /**
     * About something.
     */
    public void about() {
    }


    /**
     * Selects audiotrack. It could be just an audio file or audio from video.
     */
    public void audio() {
    }

    /**
     * Shows manual in default pdf Application.
     */
    public void getManual() {
        File manual = new File(this.getClass().getResource("/test/main.pdf").getPath()
                                       .replace("%20", " "));
        //TODO Test if works for most OS. Works with Ubuntu 18.04 and Windows
        if (Desktop.isDesktopSupported()) {
            String os = System.getProperty("os.name").toLowerCase();
            try {
                if (os.contains("linux")) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", manual.getAbsolutePath()});
                } else {
                    Desktop.getDesktop().open(manual);
                }
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Cannot load manual", e);
            }
        }
    }

    /**
     * A bit generalized method to use the filechooser in the Menu.
     *
     * @param title      Title for filechooser
     * @param filesType  Filetype, for example Image Files
     * @param extensions File Extensions in List. Format *.png for example.
     * @return The chosen file from the user. It can be null.
     */
    private File openFile(final String title,
                          final String filesType, final List<String> extensions) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);

        extensions.forEach(x -> chooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter(filesType, x)));

        return chooser.showOpenDialog(menuBar.getScene().getWindow());
    }

    /**
     * Shows in a new window the settings that can be controlled from the user.
     */
    public void loadSettingsMenu() {
        FxUtil.newWindow("/fxml/menu/SettingsMenu.fxml", "Settings",
                         menuBar.getScene().getWindow());
    }

    @FXML
    private void buildMlt() {
        String path = getSavePath("mlt-File (*.mlt)", ".mlt");
        if (path.equals("")) {
            return;
        }
        MltData data = new MltData(path, player, tracks);
        MltWriter.writeDocument(data, null, false);
    }

    public void receive(AnswerVideoDataEvent answerVideoDataEvent) {
        this.player = answerVideoDataEvent.getPlayer();
    }

    public void receive(AnswerTrackDataEvent answerTrackDataEvent) {
        this.tracks = answerTrackDataEvent.getTracks();
    }

    public void receive(AnswerLogPathEvent answerLogPathEvent) {
        this.log = answerLogPathEvent.getPaths();
    }

    public void receive(LoadProjectEvent loadProjectEvent) {
        openProject(loadProjectEvent.getPath());
    }

}
