package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.FlatArrowButton;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

class FlatSplitPaneUI$FlatSplitPaneDivider$FlatOneTouchButton
extends FlatArrowButton {
    protected final boolean left;

    protected FlatSplitPaneUI$FlatSplitPaneDivider$FlatOneTouchButton(boolean left) {
        super(1, FlatSplitPaneDivider.this.this$0.arrowType, FlatSplitPaneDivider.this.this$0.oneTouchArrowColor, null, FlatSplitPaneDivider.this.this$0.oneTouchHoverArrowColor, null, FlatSplitPaneDivider.this.this$0.oneTouchPressedArrowColor, null);
        this.setCursor(Cursor.getPredefinedCursor(0));
        ToolTipManager.sharedInstance().registerComponent(this);
        this.left = left;
    }

    @Override
    public int getDirection() {
        return FlatSplitPaneDivider.this.orientation == 0 ? (this.left ? 1 : 5) : (this.left ? 7 : 3);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        String key = FlatSplitPaneDivider.this.orientation == 0 ? (this.left ? (FlatSplitPaneDivider.this.isRightCollapsed() ? "SplitPaneDivider.expandBottomToolTipText" : "SplitPaneDivider.collapseTopToolTipText") : (FlatSplitPaneDivider.this.isLeftCollapsed() ? "SplitPaneDivider.expandTopToolTipText" : "SplitPaneDivider.collapseBottomToolTipText")) : (this.left ? (FlatSplitPaneDivider.this.isRightCollapsed() ? "SplitPaneDivider.expandRightToolTipText" : "SplitPaneDivider.collapseLeftToolTipText") : (FlatSplitPaneDivider.this.isLeftCollapsed() ? "SplitPaneDivider.expandLeftToolTipText" : "SplitPaneDivider.collapseRightToolTipText"));
        Object value = FlatSplitPaneDivider.this.splitPane.getClientProperty(key);
        if (value instanceof String) {
            return (String)value;
        }
        return UIManager.getString((Object)key, this.getLocale());
    }
}
