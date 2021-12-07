package org.newdawn.slick.muffin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import org.newdawn.slick.muffin.Muffin;
import org.newdawn.slick.util.Log;

public class WebstartMuffin
implements Muffin {
    @Override
    public void saveFile(HashMap scoreMap, String fileName) throws IOException {
        URL configURL;
        PersistenceService ps;
        try {
            ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService");
            BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
            URL baseURL = bs.getCodeBase();
            configURL = new URL(baseURL, fileName);
        }
        catch (Exception e) {
            Log.error(e);
            throw new IOException("Failed to save state: ");
        }
        try {
            ps.delete(configURL);
        }
        catch (Exception e) {
            Log.info("No exisiting Muffin Found - First Save");
        }
        try {
            ps.create(configURL, 1024L);
            FileContents fc = ps.get(configURL);
            DataOutputStream oos = new DataOutputStream(fc.getOutputStream(false));
            Set keys = scoreMap.keySet();
            for (String key : keys) {
                oos.writeUTF(key);
                if (fileName.endsWith("Number")) {
                    double value = (Double)scoreMap.get(key);
                    oos.writeDouble(value);
                    continue;
                }
                String value = (String)scoreMap.get(key);
                oos.writeUTF(value);
            }
            oos.flush();
            oos.close();
        }
        catch (Exception e) {
            Log.error(e);
            throw new IOException("Failed to store map of state data");
        }
    }

    @Override
    public HashMap loadFile(String fileName) throws IOException {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        try {
            PersistenceService ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService");
            BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
            URL baseURL = bs.getCodeBase();
            URL configURL = new URL(baseURL, fileName);
            FileContents fc = ps.get(configURL);
            DataInputStream ois = new DataInputStream(fc.getInputStream());
            if (fileName.endsWith("Number")) {
                String key;
                while ((key = ois.readUTF()) != null) {
                    double value = ois.readDouble();
                    hashMap.put(key, new Double(value));
                }
            } else {
                String key;
                while ((key = ois.readUTF()) != null) {
                    String value = ois.readUTF();
                    hashMap.put(key, value);
                }
            }
            ois.close();
        }
        catch (EOFException e) {
        }
        catch (IOException e) {
        }
        catch (Exception e) {
            Log.error(e);
            throw new IOException("Failed to load state from webstart muffin");
        }
        return hashMap;
    }
}
