package syncgod.menu;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import syncgod.pubsub.EventBus;
import syncgod.pubsub.events.LoadVideoEvent;


/**
 * SelectVideoMenu Controller.
 *
 * @author tj18b
 */
public class SelectVideoMenu implements Initializable {

    @FXML
    private Button cancel;
    @FXML
    private VBox offsetHolder;
    @FXML
    private VBox selectVideoMenu;


    private final List<String> paths = new ArrayList<>();
    private final List<OffsetSelect> offsetController = new ArrayList<>();
    private static final Logger LOG = Logger.getLogger(SelectVideoMenu.class.getName());

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
    }

    /**
     * Dynamically loads a Menu for settings offset values for corresponding videos.
     * @param paths to videos
     */
    public void setUp(final List<String> paths) {
        this.paths.addAll(paths);

        double heightBox = selectVideoMenu.getMinHeight();
        String absoluteFilePath;
        int fileNameStartIndex;

        for (int i = 0; i < paths.size(); i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu"
                    + "/OffsetSelect.fxml"));
                Parent offsetItem = loader.load();
                offsetHolder.getChildren().add(offsetItem);
                absoluteFilePath = paths.get(i);
                fileNameStartIndex = absoluteFilePath.lastIndexOf("\\") + 1;
                OffsetSelect controller = loader.getController();
                controller.setLabelPath(absoluteFilePath.substring(fileNameStartIndex));
                offsetController.add(controller);
                heightBox += controller.getHeight();
            } catch (IOException e) {
                LOG.log(Level.WARNING, "Was unable to open loadVideo Window", e);
            }
        }

        cancel.getScene().getWindow().setHeight(heightBox);
    }

    /**
     * Cancels action. Nothing is saved.
     */
    public void cancel() {
        ((Stage) cancel.getScene().getWindow()).close();
    }

    /**
     * Checks if Path to file and the offset to the video is valid.
     */
    public void finished() {
        EventBus bus = EventBus.getInstance();

        for (int i = 0; i < paths.size(); i++) {
            if (!offsetController.get(i).getCheckSelected()) {
                continue;
            }
            try {
                Long offset = Long.parseLong(offsetController.get(i).getOffset());
                if (offset < 0) {
                    return;
                }
                bus.post(new LoadVideoEvent(paths.get(i), offset));
            } catch (NumberFormatException ex) {
                LOG.log(Level.WARNING, "No valid number", ex);
            }
        }

        ((Stage) cancel.getScene().getWindow()).close();
    }
}
