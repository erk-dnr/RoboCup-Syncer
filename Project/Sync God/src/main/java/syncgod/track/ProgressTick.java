package syncgod.track;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

public final class ProgressTick {

    private final Region tickRegion;
    private final Label tickLabel;
    private final int tickHeight = 12;
    private final int tickWidth = 2;

    /**
     * Creates a progress tick.
     * @param isBigTick if tick should be big
     * @param holderSize height of parent
     */
    public ProgressTick(final boolean isBigTick, final double holderSize) {
        tickRegion = new Region();
        if (isBigTick) {
            tickRegion.setPrefHeight(tickHeight);
            tickRegion.setLayoutY(holderSize - tickHeight);
        } else {
            tickRegion.setPrefHeight(tickHeight / 2.0);
            tickRegion.setLayoutY(holderSize - tickHeight / 2.0);
        }
        tickRegion.setPrefWidth(tickWidth);
        tickRegion.getStyleClass().add("tickRegion");

        if (isBigTick) {
            tickLabel = new Label("00:00:00");
            tickLabel.setPrefHeight(30);
            tickLabel.setAlignment(Pos.TOP_CENTER);
            tickLabel.getStyleClass().add("tickLabel");
        } else {
            tickLabel = null;
        }
    }

    /**
     * Repositioning ticks.
     * @param stepWidth new step width
     */
    final void resize(final double stepWidth) {
        tickRegion.setLayoutX(stepWidth);
        if (tickLabel != null) {
            tickLabel.setLayoutX(stepWidth - tickLabel.getWidth() / 2.0);
        }
    }

    /**
     * Add tick to parent.
     * @param pane parent
     */
    public final void addToParent(final AnchorPane pane) {
        pane.getChildren().add(tickRegion);
        if (tickLabel != null) {
            pane.getChildren().add(tickLabel);
        }
    }

    /**
     * Delete from parent.
     * @param pane parent
     */
    public final void deleteFromParent(final AnchorPane pane) {
        pane.getChildren().remove(tickRegion);
        if (tickLabel != null) {
            pane.getChildren().remove(tickLabel);
        }
    }

    /**
     * Updating label to new string value.
     * @param value label text
     */
    public final void updateLabel(final String value) {
        if (tickLabel != null) {
            tickLabel.setText(value);
        }
    }
}
