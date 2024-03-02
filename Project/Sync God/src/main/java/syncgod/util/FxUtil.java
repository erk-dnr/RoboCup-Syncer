package syncgod.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Random;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.opencv.core.Mat;
import syncgod.config.Config;
import syncgod.config.ConfigValue;

/**
 * Provide general purpose methods for handling OpenCV-JavaFX data conversion. Moreover, expose some
 * "low level" methods for matching few JavaFX behavior.
 *
 * @author tj18b
 * @version 1.0
 * @since 1.0
 */
public final class FxUtil {

    private static final Logger LOG = Logger.getLogger(FxUtil.class.getName());
    private static final Random random = new Random();

    /**
     * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX.
     *
     * @param frame the {@link Mat} representing the current frame
     *
     * @return the {@link Image} to show
     */
    public static Image mat2Image(Mat frame) {
        try {
            return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Cannot convert the Mat object", e);
            return null;
        }
    }

    /**
     * Generic method for putting element running on a non-JavaFX thread on the JavaFX thread, to
     * properly update the UI.
     *
     * @param property a {@link ObjectProperty}
     * @param value    the value to set for the given {@link ObjectProperty}
     */
    public static <T> void onFxThread(final ObjectProperty<T> property, final T value) {
        Platform.runLater(() -> property.set(value));
    }

    /**
     * Converts a Mat (OpenCV) to a buffered Image.
     *
     * @param original the {@link Mat} object in BGR or gray scale.
     *
     * @return the corresponding {@link BufferedImage}
     */
    private static BufferedImage matToBufferedImage(final Mat original) {
        BufferedImage image;
        final int width = original.width();
        final int height = original.height();
        final int channels = original.channels();
        final byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);

        if (original.channels() > 1) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        } else {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }

    /**
     * Adds a node to parent with an animation effect.
     * @param node the {@link Node} to add
     * @param parent the {@link Group} to add to
     * @param animationCreator add animation
     */
    public static void addAnimating(final Node node, final Group parent,
                                    final Supplier<Animation> animationCreator) {
        parent.getChildren().add(node);
        animationCreator.get().play();
    }

    /**
     * Removes a node to parent with an animation effect.
     * @param node the {@link Node} to remove
     * @param parent the {@link Group} to remove from
     * @param animationCreator remove animation
     */
    public static void removeAnimating(final Node node, final Group parent,
                                       final Supplier<Animation> animationCreator) {
        if (parent.getChildren().contains(node)) {
            final Animation animation = animationCreator.get();
            animation.setOnFinished(finishHim -> {
                parent.getChildren().remove(node);
            });
            animation.play();
        }
    }

    /**
     * Creates a random color.
     * @return random color as hex string
     */
    public static String createRandomColor() {
        final StringBuilder color = new StringBuilder("#");
        for (int i = 0; i < 6; i++) {
            color.append(Integer.toHexString(random.nextInt(0xf)));
        }
        return color.toString();
    }

    /**
     * Helper method for initiating a stage.
     * @param root the {@link Parent} root element
     * @param title title of stage
     * @param owner owner of stage
     * @return
     */
    public static Stage initStage(final Parent root, final String title, final Window owner) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
        return stage;
    }

    /**
     * Helper method for opening a new fx window from fxml.
     * @param path from fxml file
     * @param title title of stage
     * @param owner owner of stage
     * @return FXMLLoader from fxml
     */
    public static FXMLLoader newWindow(final String path, final String title, final Window owner) {
        FXMLLoader loader = new FXMLLoader(FxUtil.class.getResource(path));
        try {
            Parent root = loader.load();
            initStage(root, title, owner);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Was unable to open Settings Window", ex);
        }
        return loader;
    }
}
