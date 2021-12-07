/*
 * Decompiled with CFR 0.150.
 */
package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.ui.StackUtils;
import java.util.function.BiPredicate;

class StackUtilsImpl
extends StackUtils {
    StackUtilsImpl() {
    }

    @Override
    boolean wasInvokedFromImpl(BiPredicate<String, String> predicate, int limit) {
        StackTraceElement[] stackTrace;
        int count = -2;
        for (StackTraceElement stackTraceElement : stackTrace = Thread.currentThread().getStackTrace()) {
            if (predicate.test(stackTraceElement.getClassName(), stackTraceElement.getMethodName())) {
                return true;
            }
            if (limit <= 0 || ++count <= limit) continue;
            return false;
        }
        return false;
    }
}

