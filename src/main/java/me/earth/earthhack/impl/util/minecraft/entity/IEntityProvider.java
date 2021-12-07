package me.earth.earthhack.impl.util.minecraft.entity;

import java.util.List;
import javax.swing.text.html.parser.Entity;
import net.minecraft.entity.player.EntityPlayer;

public interface IEntityProvider {
    public List<Entity> getEntities();

    public List<EntityPlayer> getPlayers();
}
