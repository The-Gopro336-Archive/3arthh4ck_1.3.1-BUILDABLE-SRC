/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  javax.jnlp.ServiceManager
 */
package org.newdawn.slick;

import java.io.IOException;
import java.util.HashMap;
import javax.jnlp.ServiceManager;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.muffin.FileMuffin;
import org.newdawn.slick.muffin.Muffin;
import org.newdawn.slick.muffin.WebstartMuffin;
import org.newdawn.slick.util.Log;

public class SavedState {
    private String fileName;
    private Muffin muffin;
    private HashMap numericData = new HashMap();
    private HashMap stringData = new HashMap();

    public SavedState(String fileName) throws SlickException {
        this.fileName = fileName;
        this.muffin = this.isWebstartAvailable() ? new WebstartMuffin() : new FileMuffin();
        try {
            this.load();
        }
        catch (IOException e) {
            throw new SlickException("Failed to load state on startup", e);
        }
    }

    public double getNumber(String nameOfField) {
        return this.getNumber(nameOfField, 0.0);
    }

    public double getNumber(String nameOfField, double defaultValue) {
        Double value = (Double)this.numericData.get(nameOfField);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public void setNumber(String nameOfField, double value) {
        this.numericData.put(nameOfField, new Double(value));
    }

    public String getString(String nameOfField) {
        return this.getString(nameOfField, null);
    }

    public String getString(String nameOfField, String defaultValue) {
        String value = (String)this.stringData.get(nameOfField);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public void setString(String nameOfField, String value) {
        this.stringData.put(nameOfField, value);
    }

    public void save() throws IOException {
        this.muffin.saveFile(this.numericData, this.fileName + "_Number");
        this.muffin.saveFile(this.stringData, this.fileName + "_String");
    }

    public void load() throws IOException {
        this.numericData = this.muffin.loadFile(this.fileName + "_Number");
        this.stringData = this.muffin.loadFile(this.fileName + "_String");
    }

    public void clear() {
        this.numericData.clear();
        this.stringData.clear();
    }

    private boolean isWebstartAvailable() {
        try {
            Class.forName("javax.jnlp.ServiceManager");
            ServiceManager.lookup((String)"javax.jnlp.PersistenceService");
            Log.info("Webstart detected using Muffins");
        }
        catch (Exception e) {
            Log.info("Using Local File System");
            return false;
        }
        return true;
    }
}

