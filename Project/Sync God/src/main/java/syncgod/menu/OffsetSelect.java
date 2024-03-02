package syncgod.menu;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class OffsetSelect implements Initializable {

    @FXML
    private HBox offsetSelect;
    @FXML
    private CheckBox checkSelected;
    @FXML
    private TextField textFieldOffset;
    @FXML
    private Label labelPath;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
    }

    public void setLabelPath(final String path) {
        labelPath.setText(path);
    }

    public boolean getCheckSelected() {
        return checkSelected.isSelected();
    }

    public String getOffset() {
        return textFieldOffset.getText();
    }

    public double getHeight() {
        return offsetSelect.getMinHeight();
    }
}
