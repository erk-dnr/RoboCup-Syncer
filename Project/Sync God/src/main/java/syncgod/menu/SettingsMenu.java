package syncgod.menu;

import static syncgod.config.ConfigValue.*;

import java.net.URL;

import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import syncgod.config.Config;
import syncgod.config.ConfigValue;



/**
 * SettingsMenu Controller.
 *
 * @author tj18b
 */
public class SettingsMenu implements Initializable {

    private final Pattern integerPattern = Pattern.compile("(([1-9][0-9]*)|0)?");
    private final Pattern doublePattern = Pattern.compile("(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

    /**
     * Formatter for the TextField in Spinner.
     */
    private final UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (integerPattern.matcher(newText).matches()) {
            return change;
        }
        return null;
    };

    private final UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
        String newText = change.getControlNewText();
        if (doublePattern.matcher(newText).matches()) {
            return change;
        }
        return null;
    };

    @FXML
    private TextField fieldFps;
    @FXML
    private TextField fieldSkipFrameAmount;
    @FXML
    private TextField fieldVideoWidth;
    @FXML
    private TextField fieldVideoHeight;
    @FXML
    private TextField fieldWorkerRate;
    @FXML
    private TextField fieldClockRate;
    @FXML
    private TextField fieldMouseResizeSens;

    @FXML
    private Button cancelButton;
    @FXML
    private Button applyButton;

    /**
     * Initializes the Settings Menu.
     *
     * @param url       URL
     * @param resources Resources
     */
    @Override
    public void initialize(final URL url, final ResourceBundle resources) {
        constructDoubleField(fieldFps, Fps);
        constructIntegerField(fieldSkipFrameAmount, SkipFrameAmount);
        constructIntegerField(fieldVideoWidth, VideoWidth);
        constructIntegerField(fieldVideoHeight, VideoHeight);
        constructIntegerField(fieldWorkerRate, WorkerRate);
        constructIntegerField(fieldClockRate, ClockRate);
        constructDoubleField(fieldMouseResizeSens, MouseResizeSens);
    }

    private void constructIntegerField(final TextField textField, final ConfigValue configValue) {
        IntegerStringConverter converter = new IntegerStringConverter();
        TextFormatter<Integer> formatter
                = new TextFormatter<>(converter, (int) Config.get(configValue), integerFilter);
        textField.setTextFormatter(formatter);
        textField.textProperty()
                .addListener(((observable, oldValue, newValue) -> applyButton.setDisable(false)));
    }

    private void constructDoubleField(final TextField textField, final ConfigValue configValue) {
        DoubleStringConverter converter = new DoubleStringConverter();
        TextFormatter<Double> formatter
                = new TextFormatter<>(converter, Config.get(configValue), doubleFilter);
        textField.setTextFormatter(formatter);
        textField.textProperty()
                .addListener(((observable, oldValue, newValue) -> applyButton.setDisable(false)));
    }

    /**
     * Sets every setting to default value.
     */
    public void onClickReset() {
        Config.setToDefault();
        resetDoubleField(fieldFps, Fps);
        resetIntegerField(fieldSkipFrameAmount, SkipFrameAmount);
        resetIntegerField(fieldVideoWidth, VideoWidth);
        resetIntegerField(fieldVideoHeight, VideoHeight);
        resetIntegerField(fieldWorkerRate, WorkerRate);
        resetIntegerField(fieldClockRate, ClockRate);
        resetDoubleField(fieldMouseResizeSens, MouseResizeSens);
    }

    private void resetDoubleField(final TextField textField, final ConfigValue configValue) {
        textField.setText(String.valueOf(Config.get(configValue)));
    }

    private void resetIntegerField(final TextField textField, final ConfigValue configValue) {
        textField.setText(String.valueOf((int) Config.get(configValue)));
    }

    private void setConfigValue(final TextField textField, final ConfigValue configValue) {
        Config.set(configValue, Double.valueOf(textField.getText()));
    }

    /**
     * Saves the changes.
     */
    @FXML
    private void onClickApply() {
        setConfigValue(fieldFps, Fps);
        setConfigValue(fieldSkipFrameAmount, SkipFrameAmount);
        setConfigValue(fieldVideoWidth, VideoWidth);
        setConfigValue(fieldVideoHeight, VideoHeight);
        setConfigValue(fieldWorkerRate, WorkerRate);
        setConfigValue(fieldClockRate, ClockRate);
        setConfigValue(fieldMouseResizeSens, MouseResizeSens);
        applyButton.setDisable(true);
        Config.saveConfig();
    }

    /**
     * Saves changes and quits.
     */
    @FXML
    private void onClickOk() {
        onClickApply();
        onClickCancel();
    }

    @FXML
    private void onClickCancel() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }
}
