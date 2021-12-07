package com.formdev.flatlaf;

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.plaf.InputMapUIResource;

class FlatInputMaps$LazyInputMapEx
implements UIDefaults.LazyValue {
    private final Object[][] bindingsArray;

    FlatInputMaps$LazyInputMapEx(Object[] ... bindingsArray) {
        this.bindingsArray = bindingsArray;
    }

    @Override
    public Object createValue(UIDefaults table) {
        InputMapUIResource inputMap = new InputMapUIResource();
        for (Object[] bindings : this.bindingsArray) {
            LookAndFeel.loadKeyBindings(inputMap, bindings);
        }
        return inputMap;
    }
}
