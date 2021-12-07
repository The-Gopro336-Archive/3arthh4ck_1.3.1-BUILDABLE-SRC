package com.formdev.flatlaf;

import java.util.function.BooleanSupplier;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.UIDefaults;

class FlatInputMaps$LazyModifyInputMap
implements UIDefaults.LazyValue {
    private final BooleanSupplier condition;
    private final Object baseInputMap;
    private final Object[] bindings;

    FlatInputMaps$LazyModifyInputMap(BooleanSupplier condition, Object baseInputMap, Object[] bindings) {
        this.condition = condition;
        this.baseInputMap = baseInputMap;
        this.bindings = bindings;
    }

    @Override
    public Object createValue(UIDefaults table) {
        InputMap inputMap;
        InputMap inputMap2 = inputMap = this.baseInputMap instanceof UIDefaults.LazyValue ? (InputMap)((UIDefaults.LazyValue)this.baseInputMap).createValue(table) : (InputMap)this.baseInputMap;
        if (this.condition != null && !this.condition.getAsBoolean()) {
            return inputMap;
        }
        for (int i = 0; i < this.bindings.length; i += 2) {
            KeyStroke keyStroke = KeyStroke.getKeyStroke((String)this.bindings[i]);
            if (this.bindings[i + 1] != null) {
                inputMap.put(keyStroke, this.bindings[i + 1]);
                continue;
            }
            inputMap.remove(keyStroke);
        }
        return inputMap;
    }
}
