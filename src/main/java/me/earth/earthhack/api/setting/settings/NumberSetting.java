package me.earth.earthhack.api.setting.settings;

import com.google.gson.JsonElement;
import java.text.NumberFormat;
import java.text.ParseException;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingResult;

public class NumberSetting<N extends Number>
extends Setting<N> {
    private final boolean restriction;
    private final String description;
    private final boolean floating;
    private final N max;
    private final N min;

    public NumberSetting(String nameIn, N initialValue) {
        super(nameIn, initialValue);
        Number[] minMax = this.getDefaultMinMax();
        this.min = minMax[0];
        this.max = minMax[1];
        this.restriction = false;
        this.description = this.generateOutPut();
        this.floating = this.isDoubleOrFloat();
    }

    public NumberSetting(String nameIn, N initialValue, N min, N max) {
        super(nameIn, initialValue);
        this.min = min;
        this.max = max;
        this.restriction = true;
        this.description = this.generateOutPut();
        this.floating = this.isDoubleOrFloat();
    }

    @Override
    public void fromJson(JsonElement element) {
        this.setValue(this.numberToValue(element.getAsNumber()));
    }

    @Override
    public SettingResult fromString(String string) {
        if (string == null) {
            return new SettingResult(false, "Value was null.");
        }
        String noComma = string.replace(',', '.');
        try {
            Number parsed = NumberFormat.getInstance().parse(noComma);
            N result = this.numberToValue(parsed);
            if (result == null) {
                return new SettingResult(false, "The numberToValue method returned null.");
            }
            if (this.inBounds(result)) {
                this.setValue(result);
                return SettingResult.SUCCESSFUL;
            }
        }
        catch (ClassCastException | ParseException e) {
            e.printStackTrace();
            return new SettingResult(false, string + " could not be parsed.");
        }
        return new SettingResult(false, string + " is out of bounds (" + this.min + "-" + this.max + ")");
    }

    @Override
    public String getInputs(String string) {
        if (string == null || string.isEmpty()) {
            return this.description;
        }
        return "";
    }

    @Override
    public void setValue(N value, boolean withEvent) {
        if (this.inBounds(value)) {
            super.setValue(value, withEvent);
        }
    }

    public boolean inBounds(N value) {
        return !this.restriction || !(((Number)value).doubleValue() < ((Number)this.min).doubleValue()) && !(((Number)value).doubleValue() > ((Number)this.max).doubleValue());
    }

    public boolean hasRestriction() {
        return this.restriction;
    }

    public N getMax() {
        return this.max;
    }

    public N getMin() {
        return this.min;
    }

    public N numberToValue(Number number) {
        Class<?> type = ((Number)this.initial).getClass();
        Number result = null;
        if (type == Integer.class) {
            result = number.intValue();
        } else if (type == Float.class) {
            result = Float.valueOf(number.floatValue());
        } else if (type == Double.class) {
            result = number.doubleValue();
        } else if (type == Short.class) {
            result = number.shortValue();
        } else if (type == Byte.class) {
            result = number.byteValue();
        } else if (type == Long.class) {
            result = number.longValue();
        }
        return (N)result;
    }

    public boolean isFloating() {
        return this.floating;
    }

    private N[] getDefaultMinMax() {
        Class<?> type = ((Number)this.initial).getClass();
        Object[] result = new Object[2];
        if (type == Integer.class) {
            result[0] = Integer.MIN_VALUE;
            result[1] = Integer.MAX_VALUE;
        } else if (type == Float.class) {
            result[0] = Float.valueOf(Float.MIN_VALUE);
            result[1] = Float.valueOf(Float.MAX_VALUE);
        } else if (type == Double.class) {
            result[0] = Double.MIN_VALUE;
            result[1] = Double.MAX_VALUE;
        } else if (type == Short.class) {
            result[0] = (short)Short.MIN_VALUE;
            result[1] = (short)Short.MAX_VALUE;
        } else if (type == Byte.class) {
            result[0] = (byte)-128;
            result[1] = (byte)127;
        } else {
            result[0] = Long.MIN_VALUE;
            result[1] = Long.MAX_VALUE;
        }
        return new Number[]{(Number)result[0], (Number)result[1]};
    }

    private String generateOutPut() {
        if (this.restriction) {
            return "<" + this.min.toString() + " - " + this.max.toString() + ">";
        }
        return "<-5, 1.0, 10 ... 1337>";
    }

    private boolean isDoubleOrFloat() {
        Class<?> clazz = ((Number)this.initial).getClass();
        return clazz == Double.class || clazz == Float.class;
    }
}
