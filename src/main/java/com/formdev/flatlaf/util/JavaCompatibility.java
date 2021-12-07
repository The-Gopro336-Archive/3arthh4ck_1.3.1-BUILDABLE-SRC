/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf.util;

import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JComponent;

public class JavaCompatibility {
    private static Method drawStringUnderlineCharAtMethod;
    private static Method getClippedStringMethod;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void drawStringUnderlineCharAt(JComponent c, Graphics g, String text, int underlinedIndex, int x, int y) {
        Class<JavaCompatibility> class_ = JavaCompatibility.class;
        synchronized (JavaCompatibility.class) {
            if (drawStringUnderlineCharAtMethod == null) {
                try {
                    Class[] arrclass;
                    Class<?> cls = Class.forName(SystemInfo.isJava_9_orLater ? "javax.swing.plaf.basic.BasicGraphicsUtils" : "sun.swing.SwingUtilities2");
                    if (SystemInfo.isJava_9_orLater) {
                        Class[] arrclass2 = new Class[6];
                        arrclass2[0] = JComponent.class;
                        arrclass2[1] = Graphics2D.class;
                        arrclass2[2] = String.class;
                        arrclass2[3] = Integer.TYPE;
                        arrclass2[4] = Float.TYPE;
                        arrclass = arrclass2;
                        arrclass2[5] = Float.TYPE;
                    } else {
                        Class[] arrclass3 = new Class[6];
                        arrclass3[0] = JComponent.class;
                        arrclass3[1] = Graphics.class;
                        arrclass3[2] = String.class;
                        arrclass3[3] = Integer.TYPE;
                        arrclass3[4] = Integer.TYPE;
                        arrclass = arrclass3;
                        arrclass3[5] = Integer.TYPE;
                    }
                    drawStringUnderlineCharAtMethod = cls.getMethod("drawStringUnderlineCharAt", arrclass);
                }
                catch (Exception ex) {
                    LoggingFacade.INSTANCE.logSevere(null, ex);
                    throw new RuntimeException(ex);
                }
            }
            // ** MonitorExit[var6_6] (shouldn't be in output)
            try {
                if (SystemInfo.isJava_9_orLater) {
                    drawStringUnderlineCharAtMethod.invoke(null, c, g, text, underlinedIndex, Float.valueOf(x), Float.valueOf(y));
                } else {
                    drawStringUnderlineCharAtMethod.invoke(null, c, g, text, underlinedIndex, x, y);
                }
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LoggingFacade.INSTANCE.logSevere(null, ex);
                throw new RuntimeException(ex);
            }
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getClippedString(JComponent c, FontMetrics fm, String string, int availTextWidth) {
        Class<JavaCompatibility> class_ = JavaCompatibility.class;
        synchronized (JavaCompatibility.class) {
            if (getClippedStringMethod == null) {
                try {
                    Class<?> cls = Class.forName(SystemInfo.isJava_9_orLater ? "javax.swing.plaf.basic.BasicGraphicsUtils" : "sun.swing.SwingUtilities2");
                    getClippedStringMethod = cls.getMethod(SystemInfo.isJava_9_orLater ? "getClippedString" : "clipStringIfNecessary", JComponent.class, FontMetrics.class, String.class, Integer.TYPE);
                }
                catch (Exception ex) {
                    LoggingFacade.INSTANCE.logSevere(null, ex);
                    throw new RuntimeException(ex);
                }
            }
            // ** MonitorExit[var4_4] (shouldn't be in output)
            try {
                return (String)getClippedStringMethod.invoke(null, c, fm, string, availTextWidth);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LoggingFacade.INSTANCE.logSevere(null, ex);
                throw new RuntimeException(ex);
            }
        }
    }
}

