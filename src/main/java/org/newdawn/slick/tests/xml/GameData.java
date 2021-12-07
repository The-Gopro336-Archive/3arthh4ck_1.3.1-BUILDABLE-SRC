/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.tests.xml;

import java.util.ArrayList;
import org.newdawn.slick.tests.xml.Entity;

public class GameData {
    private ArrayList entities = new ArrayList();

    private void add(Entity entity) {
        this.entities.add(entity);
    }

    public void dump(String prefix) {
        System.out.println(prefix + "GameData");
        for (int i = 0; i < this.entities.size(); ++i) {
            ((Entity)this.entities.get(i)).dump(prefix + "\t");
        }
    }
}

