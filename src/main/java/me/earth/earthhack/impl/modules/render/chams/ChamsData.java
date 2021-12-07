package me.earth.earthhack.impl.modules.render.chams;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.render.chams.Chams;

final class ChamsData
extends DefaultData<Chams> {
    public ChamsData(Chams module) {
        super(module);
        this.register(module.mode, "Switch between Normal and CSGO like chams.");
        this.register(module.self, "Render chams for yourself.");
        this.register(module.players, "Render chams for players.");
        this.register(module.animals, "Render chams for animals.");
        this.register(module.monsters, "Render chams for monsters.");
        this.register(module.texture, "Render textured chams.");
        this.register(module.xqz, "Renders chams through walls");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Render Entities through walls.";
    }
}
