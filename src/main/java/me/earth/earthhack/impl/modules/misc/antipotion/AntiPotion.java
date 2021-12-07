/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.init.MobEffects
 *  net.minecraft.potion.Potion
 */
package me.earth.earthhack.impl.modules.misc.antipotion;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.misc.antipotion.AntiPotionData;
import me.earth.earthhack.impl.modules.misc.antipotion.ListenerUpdates;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;

public class AntiPotion
extends Module {
    public AntiPotion() {
        super("AntiPotion", Category.Misc);
        AntiPotionData data = new AntiPotionData(this);
        this.setData(data);
        for (Potion potion : Potion.REGISTRY) {
            boolean value = potion == MobEffects.LEVITATION;
            String name = AntiPotion.getPotionString(potion);
            BooleanSetting s = this.register(new BooleanSetting(name, value));
            data.register(s, "Removes " + name + " potion effects.");
        }
        this.listeners.add(new ListenerUpdates(this));
    }

    public static String getPotionString(Potion potion) {
        return I18n.format((String)potion.getName(), (Object[])new Object[0]);
    }
}

