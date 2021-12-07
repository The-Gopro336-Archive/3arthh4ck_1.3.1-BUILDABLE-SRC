package org.spongepowered.asm.lib;

import org.spongepowered.asm.lib.Label;

class Handler {
    Label start;
    Label end;
    Label handler;
    String desc;
    int type;
    Handler next;

    Handler() {
    }

    static Handler remove(Handler h, Label start, Label end) {
        int e;
        if (h == null) {
            return null;
        }
        h.next = Handler.remove(h.next, start, end);
        int hstart = h.start.position;
        int hend = h.end.position;
        int s = start.position;
        int n = e = end == null ? Integer.MAX_VALUE : end.position;
        if (s < hend && e > hstart) {
            if (s <= hstart) {
                if (e >= hend) {
                    h = h.next;
                } else {
                    h.start = end;
                }
            } else if (e >= hend) {
                h.end = start;
            } else {
                Handler g = new Handler();
                g.start = end;
                g.end = h.end;
                g.handler = h.handler;
                g.desc = h.desc;
                g.type = h.type;
                g.next = h.next;
                h.end = start;
                h.next = g;
            }
        }
        return h;
    }
}
