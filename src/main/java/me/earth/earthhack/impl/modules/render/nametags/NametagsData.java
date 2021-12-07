/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.render.nametags;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.render.nametags.Nametags;

final class NametagsData
extends DefaultData<Nametags> {
    public NametagsData(Nametags module) {
        super(module);
        this.register(module.health, "Renders the players health.");
        this.register(module.ping, "Renders the players ping.");
        this.register(module.id, "Renders the players EntityID.");
        this.register(module.itemStack, "Renders the players currently held ItemStacks name.");
        this.register(module.armor, "Renders the players armor.");
        this.register(module.gameMode, "Displays the players current gamemode.");
        this.register(module.durability, "Displays the Durability of the Armor the player is currently wearing.");
        this.register(module.invisibles, "Renders Nametags for Invisible players.");
        this.register(module.pops, "Displays the players totem pops.");
        this.register(module.fov, "Only renders Nametags within your FOV.");
        this.register(module.scale, "Scale of the Nametags.");
        this.register(module.burrow, "Shows when players are burrowed.");
        this.register(module.debug, "Displays Entity-Ids for every Entity.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Displays Name, Health and Items above players.";
    }
}

