/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  com.mojang.text2speech.Narrator
 */
package me.earth.earthhack.impl.modules.combat.bowkill;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mojang.text2speech.Narrator;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.EntityChunkEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.bowkill.BowKiller;
import me.earth.earthhack.impl.util.text.ChatUtil;

final class ListenerEntityChunk
extends ModuleListener<BowKiller, EntityChunkEvent> {
    private final Narrator narrator = Narrator.getNarrator();

    public ListenerEntityChunk(BowKiller module) {
        super(module, EntityChunkEvent.class);
    }

    @Override
    public void invoke(EntityChunkEvent event) {
        if (!((BowKiller)this.module).oppSpotted.getValue().booleanValue()) {
            return;
        }
        if (event.getStage() == Stage.PRE && event.getEntity() != null && ((BowKiller)this.module).isValid(event.getEntity())) {
            ChatUtil.sendMessage((Object)ChatFormatting.RED + "Opp detected I repeat opp detected!");
            if (!((BowKiller)this.module).hasEntity(event.getEntity().getUniqueID().toString())) {
                this.narrator.clear();
                this.narrator.say("Ah pu  Detected!");
                ((BowKiller)this.module).entityDataArrayList.add(new BowKiller.EntityData(event.getEntity().getUniqueID().toString(), System.currentTimeMillis()));
            }
        }
    }
}

