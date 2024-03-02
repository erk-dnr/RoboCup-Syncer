package syncgod.scene;

import static syncgod.config.UtilValue.LIB_PATH;

import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import nu.pattern.OpenCV;
import syncgod.config.Config;



/**
 * Loads opencv library and root element.
 * @author tj18b
 */
public class Main extends Application {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    @Override
    public void start(final Stage stage) throws Exception {
        Config.loadAllProperties();

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("linux")) {
            loadLibrary();
        } else {
            OpenCV.loadShared();
        }

        Parent root = FXMLLoader.load(getClass()
                .getResource("/fxml/Scene.fxml"));
        root.getStylesheets().add("/styles/Styles.css");
        Scene scene = new Scene(root);
        stage.setTitle("Sync God");
        stage.setMinHeight(720);
        stage.setMinWidth(1280);
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();
    }

    private static void loadLibrary() {
        String libFilePath = Config.getUtilProperty(LIB_PATH);
        File libFile;
        if (libFilePath == null) {
            libFile = findLibFile();
            if (libFile == null) {
                libFile = selectLibFile();
            }
        } else {
            libFile = new File(Config.getUtilProperty(LIB_PATH));
        }

        if (!libFile.exists()) {
            LOG.log(Level.WARNING, "OpenCV library file could not be found");
            Platform.exit();
            System.exit(0);
        }
        System.load(libFile.getAbsolutePath());
        Config.setUtilProperty(LIB_PATH, libFile.getAbsolutePath());
        Config.saveUtilProperty();
    }

    private static File selectLibFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select OpenCV Library");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OpenCV","*.so"));
        File cvFile = chooser.showOpenDialog(null);
        if (cvFile != null) {
            return cvFile;
        } else {
            Platform.exit();
            System.exit(0);
        }
        return null;
    }

    private static File findLibFile() {
        File[] list = new File(Paths.get("").toFile().getAbsolutePath()).listFiles();
        String libFileNamePattern = "(.*)(lib|java|opencv)(.*)\\.so";
        if (list != null) {
            for (File fil : list) {
                if (fil.getName().matches(libFileNamePattern)) {
                    return fil;
                }
            }
        }
        return null;
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support.
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }
}
