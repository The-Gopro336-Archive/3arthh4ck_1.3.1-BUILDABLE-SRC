/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.font.effects;

import java.util.List;
import org.newdawn.slick.font.effects.Effect;

public interface ConfigurableEffect
extends Effect {
    public List getValues();

    public void setValues(List var1);

    public static interface Value {
        public String getName();

        public void setString(String var1);

        public String getString();

        public Object getObject();

        public void showDialog();
    }
}

