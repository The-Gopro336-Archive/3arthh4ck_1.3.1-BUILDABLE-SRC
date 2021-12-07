/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.tests.xml;

import org.newdawn.slick.tests.xml.Inventory;
import org.newdawn.slick.tests.xml.Stats;

public class Entity {
    private float x;
    private float y;
    private Inventory invent;
    private Stats stats;

    private void add(Inventory inventory) {
        this.invent = inventory;
    }

    private void add(Stats stats) {
        this.stats = stats;
    }

    public void dump(String prefix) {
        System.out.println(prefix + "Entity " + this.x + "," + this.y);
        this.invent.dump(prefix + "\t");
        this.stats.dump(prefix + "\t");
    }
}

