package org.newdawn.slick.opengl;

import java.io.IOException;
import java.util.ArrayList;

public class CompositeIOException
extends IOException {
    private ArrayList exceptions = new ArrayList();

    public void addException(Exception e) {
        this.exceptions.add(e);
    }

    @Override
    public String getMessage() {
        String msg = "Composite Exception: \n";
        for (int i = 0; i < this.exceptions.size(); ++i) {
            msg = msg + "\t" + ((IOException)this.exceptions.get(i)).getMessage() + "\n";
        }
        return msg;
    }
}
