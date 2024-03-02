package syncgod.menu;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.testfx.api.FxAssert.verifyThat;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import syncgod.config.Config;
import syncgod.config.ConfigValue;


/**
 *Tests for SettingsMenu Class.
 * @author Darus
 */
public class SettingsMenuTest extends ApplicationTest {


    private Parent root;

    @Override
    public void start(Stage stage) throws IOException {
        root = FXMLLoader.load(SettingsMenu.class.getResource("/fxml/menu/SettingsMenu.fxml"));
        Scene scene = new Scene(root, 720, 1280);
        stage.setScene(scene);
        stage.show();
    }

    /**
     *
     *
     */
    @Before
    public void setUp() {
    }

    /**
     *
     *
     */
    @After
    public void tearDown() {
    }

    @Test
    public void testIntegerTextFields() {
        
        TextField fieldVideoWidth = from(root).lookup("#fieldVideoWidth").query();
        String original = fieldVideoWidth.getText();

        fieldVideoWidth.setText("H");
        verifyThat(fieldVideoWidth.getText(), is(equalTo(original)));

        //One Textfield should be sufficient. All other Integer Textfields are the same.
        String input = "200";
        fieldVideoWidth.setText(input);
        verifyThat(fieldVideoWidth.getText(), is(equalTo(input)));

        input = "200.0";
        fieldVideoWidth.setText(input);
        verifyThat(fieldVideoWidth.getText(), is(equalTo("200")));
    }


    @Test
    public void testDoubleTextFields() {

        TextField fieldFps = from(root).lookup("#fieldFps").query();
        String original = fieldFps.getText();
        String input = "30.1";

        fieldFps.setText("H");

        verifyThat(fieldFps.getText(), is(equalTo(original)));

        fieldFps.setText(input);

        verifyThat(fieldFps.getText(), is(equalTo(input)));

        input = "300";

        fieldFps.setText(input);

        verifyThat(fieldFps.getText(), is(equalTo("300.0")));
    }

    @Test
    public void testDefaultConfig() {
        TextField fieldFps = from(root).lookup("#fieldFps").query();

        Config.setToDefault();                  //Maybe mock

        String defaultFps = Config.getAsString(ConfigValue.Fps);

        fieldFps.setText("20");

        Button applyButton = from(root).lookup("#applyButton").query();

        applyButton.fire();

        verifyThat(fieldFps.getText(), is(equalTo("20.0")));
        Button resetButton = from(root).lookup("#resetButton").query();

        resetButton.fire();

        verifyThat(fieldFps.getText(), is(equalTo(defaultFps)));
    }
}