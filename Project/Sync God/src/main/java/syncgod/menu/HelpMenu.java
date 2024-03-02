package syncgod.menu;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Help Menu Controller.
 */

public class HelpMenu implements Initializable {

    @FXML
    private ImageView imgView;

    @FXML
    private WebView webView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imgView.setImage(new Image(getResourceUrl("/help/images/helpImg.png")));
        loadHtml(getResourceUrl("/help/html/main.html"));
    }

    /**
     * Generates resource URL to reduce redundancy.
     * @param path Path to resource
     * @return Resource URL as String
     */
    private String getResourceUrl(String path) {
        return getClass().getResource(path).toString();
    }

    /**
     * Loads given file into the WebView.
     * @param url URL to file
     */
    private void loadHtml(String url) {
        WebEngine engine = webView.getEngine();
        engine.load(url);
    }

    /**
     * Loads help content related to video player.
     */
    public void videoPaneClicked() {
        loadHtml(getResourceUrl("/help/html/videoplayer.html"));
    }

    /**
     * Loads help content related to logs.
     */
    public void logPaneClicked() {
        loadHtml(getResourceUrl("/help/html/logpane.html"));
    }

    /**
     * Loads help content related to editing track boxes.
     */
    public void trackPaneClicked() {
        loadHtml(getResourceUrl("/help/html/trackpane.html"));
    }
}
