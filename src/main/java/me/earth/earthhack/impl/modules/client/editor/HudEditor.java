package me.earth.earthhack.impl.modules.client.editor;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.gui.hud.HudEditorGui;

public class HudEditor
extends Module {
    private final HudEditorGui GUI = new HudEditorGui();

    public HudEditor() {
        super("HudEditor", Category.Client);
    }

    @Override
    public void onEnable() {
        if (HudEditor.mc.currentScreen == null) {
            mc.displayGuiScreen(this.GUI);
        }
    }
}
