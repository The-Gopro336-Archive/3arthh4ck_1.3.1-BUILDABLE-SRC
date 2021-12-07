/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 */
package me.earth.earthhack.api.setting.settings;

import com.google.gson.JsonElement;
import java.awt.Color;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingEvent;
import me.earth.earthhack.api.setting.event.SettingResult;
import me.earth.earthhack.api.util.TextUtil;

public class ColorSetting
extends Setting<Color> {
    private int red;
    private int green;
    private int blue;
    private int alpha;
    private boolean sync;
    private boolean rainbow;
    private boolean staticRainbow;
    private float rainbowSpeed = 100.0f;
    private float rainbowSaturation = 100.0f;
    private float rainbowBrightness = 100.0f;
    private Color mutableInitial;

    public ColorSetting(String nameIn, Color initialValue) {
        super(nameIn, initialValue);
        this.mutableInitial = initialValue;
        this.red = initialValue.getRed();
        this.green = initialValue.getGreen();
        this.blue = initialValue.getBlue();
        this.alpha = initialValue.getAlpha();
    }

    public void setInitial(Color color) {
        this.mutableInitial = color;
    }

    @Override
    public Color getInitial() {
        return this.mutableInitial;
    }

    @Override
    public void reset() {
        this.value = this.mutableInitial;
    }

    @Override
    public void setValue(Color value, boolean withEvent) {
        if (withEvent) {
            SettingEvent<Color> event = this.onChange(new SettingEvent<Color>(this, value));
            if (!event.isCancelled()) {
                this.setValueRGBA(event.getValue());
            }
        } else {
            this.setValueRGBA(value);
        }
    }

    public void setValueAlpha(Color value) {
        Color newColor = new Color(value.getRed(), value.getGreen(), value.getBlue(), ((Color)this.getValue()).getAlpha());
        this.setValueRGBA(newColor);
    }

    @Override
    public void fromJson(JsonElement element) {
        String parse = element.getAsString();
        if (parse.contains("-")) {
            String[] values = parse.split("-");
            if (values.length > 6) {
                int color = 0;
                try {
                    color = (int)Long.parseLong(values[0], 16);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                this.setValue(new Color(color, values[0].length() > 6));
                boolean syncBuf = false;
                try {
                    syncBuf = Boolean.parseBoolean(values[1]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                this.setSync(syncBuf);
                boolean rainbowBuf = false;
                try {
                    rainbowBuf = Boolean.parseBoolean(values[2]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                this.setRainbow(rainbowBuf);
                boolean rainbowStaticBuf = false;
                try {
                    rainbowStaticBuf = Boolean.parseBoolean(values[3]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                this.setStaticRainbow(rainbowStaticBuf);
                float speed = 0.0f;
                try {
                    speed = (int)Float.parseFloat(values[4]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                this.setRainbowSpeed(speed);
                float saturation = 0.0f;
                try {
                    saturation = (int)Float.parseFloat(values[5]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                this.setRainbowSaturation(saturation);
                float brightness = 0.0f;
                try {
                    brightness = (int)Float.parseFloat(values[6]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                this.setRainbowBrightness(brightness);
            }
        } else {
            int color = 0;
            try {
                color = (int)Long.parseLong(parse, 16);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            this.setValue(new Color(color, parse.length() > 6));
        }
    }

    @Override
    public String toJson() {
        return TextUtil.get32BitString(((Color)this.value).getRGB()) + "-" + this.isSync() + "-" + this.isRainbow() + "-" + this.isStaticRainbow() + "-" + this.getRainbowSpeed() + "-" + this.getRainbowSaturation() + "-" + this.getRainbowBrightness();
    }

    @Override
    public SettingResult fromString(String string) {
        if (string.contains("-")) {
            String[] values = string.split("-");
            if (values.length > 6) {
                float brightness;
                float saturation;
                float speed;
                boolean rainbowStaticBuf;
                boolean rainbowBuf;
                boolean syncBuf;
                int color;
                try {
                    color = (int)Long.parseLong(values[0], 16);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return new SettingResult(false, e.getMessage());
                }
                this.setValue(new Color(color, values[0].length() > 6));
                try {
                    syncBuf = Boolean.parseBoolean(values[1]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return new SettingResult(false, e.getMessage());
                }
                this.setSync(syncBuf);
                try {
                    rainbowBuf = Boolean.parseBoolean(values[2]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return new SettingResult(false, e.getMessage());
                }
                this.setRainbow(rainbowBuf);
                try {
                    rainbowStaticBuf = Boolean.parseBoolean(values[3]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return new SettingResult(false, e.getMessage());
                }
                this.setStaticRainbow(rainbowStaticBuf);
                try {
                    speed = (int)Float.parseFloat(values[4]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return new SettingResult(false, e.getMessage());
                }
                this.setRainbowSpeed(speed);
                try {
                    saturation = (int)Float.parseFloat(values[5]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return new SettingResult(false, e.getMessage());
                }
                this.setRainbowSaturation(saturation);
                try {
                    brightness = (int)Float.parseFloat(values[6]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return new SettingResult(false, e.getMessage());
                }
                this.setRainbowBrightness(brightness);
            }
        } else {
            int color;
            try {
                color = (int)Long.parseLong(string, 16);
            }
            catch (Exception e) {
                e.printStackTrace();
                return new SettingResult(false, e.getMessage());
            }
            this.setValue(new Color(color, string.length() > 6));
        }
        return SettingResult.SUCCESSFUL;
    }

    @Override
    public String getInputs(String string) {
        if (string == null || string.isEmpty()) {
            return "<hex-string>";
        }
        return "";
    }

    public int getRed() {
        return this.red;
    }

    public float getR() {
        return (float)this.red / 255.0f;
    }

    public void setRed(int red) {
        this.red = red;
        this.setValue(new Color(red, this.blue, this.green, this.alpha));
    }

    public int getGreen() {
        return this.green;
    }

    public float getG() {
        return (float)this.green / 255.0f;
    }

    public void setGreen(int green) {
        this.green = green;
        this.setValue(new Color(this.red, this.blue, green, this.alpha));
    }

    public int getBlue() {
        return this.blue;
    }

    public float getB() {
        return (float)this.blue / 255.0f;
    }

    public void setBlue(int blue) {
        this.blue = blue;
        this.setValue(new Color(this.red, blue, this.green, this.alpha));
    }

    public int getAlpha() {
        return this.alpha;
    }

    public float getA() {
        return (float)this.alpha / 255.0f;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
        this.setValue(new Color(this.red, this.blue, this.green, alpha));
    }

    public int getRGB() {
        return ((Color)this.value).getRGB();
    }

    public boolean isSync() {
        return this.sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public boolean isRainbow() {
        return this.rainbow;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public float getRainbowSpeed() {
        return this.rainbowSpeed;
    }

    public void setRainbowSpeed(float rainbowSpeed) {
        this.rainbowSpeed = rainbowSpeed;
    }

    public float getRainbowSaturation() {
        return this.rainbowSaturation;
    }

    public void setRainbowSaturation(float rainbowSaturation) {
        this.rainbowSaturation = rainbowSaturation;
    }

    public float getRainbowBrightness() {
        return this.rainbowBrightness;
    }

    public void setRainbowBrightness(float rainbowBrightness) {
        this.rainbowBrightness = rainbowBrightness;
    }

    public boolean isStaticRainbow() {
        return this.staticRainbow;
    }

    public void setStaticRainbow(boolean staticRainbow) {
        this.staticRainbow = staticRainbow;
    }

    private void setValueRGBA(Color value) {
        this.value = value;
        this.red = value.getRed();
        this.blue = value.getBlue();
        this.green = value.getGreen();
        this.alpha = value.getAlpha();
    }
}

