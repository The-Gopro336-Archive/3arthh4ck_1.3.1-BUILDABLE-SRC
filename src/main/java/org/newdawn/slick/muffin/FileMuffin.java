package org.newdawn.slick.muffin;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import org.newdawn.slick.muffin.Muffin;
import org.newdawn.slick.util.Log;

public class FileMuffin
implements Muffin {
    @Override
    public void saveFile(HashMap scoreMap, String fileName) throws IOException {
        String userHome = System.getProperty("user.home");
        File file = new File(userHome);
        if (!(file = new File(file, ".java")).exists()) {
            file.mkdir();
        }
        file = new File(file, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(scoreMap);
        oos.close();
    }

    @Override
    public HashMap loadFile(String fileName) throws IOException {
        HashMap hashMap = new HashMap();
        String userHome = System.getProperty("user.home");
        File file = new File(userHome);
        file = new File(file, ".java");
        if ((file = new File(file, fileName)).exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                hashMap = (HashMap)ois.readObject();
                ois.close();
            }
            catch (EOFException e) {
            }
            catch (ClassNotFoundException e) {
                Log.error(e);
                throw new IOException("Failed to pull state from store - class not found");
            }
        }
        return hashMap;
    }
}
