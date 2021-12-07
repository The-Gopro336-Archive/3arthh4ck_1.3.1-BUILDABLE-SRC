/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.util.ReferenceCounted
 */
package me.earth.earthhack.impl.commands.packet.util;

import io.netty.util.ReferenceCounted;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BufferUtil {
    public static void release(List<Object> objects) {
        for (Object o : objects) {
            if (!(o instanceof ReferenceCounted)) continue;
            BufferUtil.releaseBuffer((ReferenceCounted)o);
        }
    }

    public static void release(Object ... objects) {
        for (Object o : objects) {
            if (!(o instanceof ReferenceCounted)) continue;
            BufferUtil.releaseBuffer((ReferenceCounted)o);
        }
    }

    public static void releaseFields(Object o) {
        for (Class<?> clazz = o.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Field f : clazz.getDeclaredFields()) {
                if (!ReferenceCounted.class.isAssignableFrom(f.getType())) continue;
                try {
                    f.setAccessible(true);
                    ReferenceCounted buffer = (ReferenceCounted)f.get(o);
                    if (buffer == null) continue;
                    BufferUtil.releaseBuffer(buffer);
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<Object> saveReleasableFields(Object object) {
        ArrayList<Object> objects = new ArrayList<Object>(2);
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!ReferenceCounted.class.isAssignableFrom(field.getType())) continue;
                try {
                    field.setAccessible(true);
                    objects.add(field.get(object));
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return objects;
    }

    public static void releaseBuffer(ReferenceCounted buffer) {
        buffer.release(buffer.refCnt());
    }
}

