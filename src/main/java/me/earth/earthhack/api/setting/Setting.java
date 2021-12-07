package me.earth.earthhack.api.setting;

import com.google.gson.JsonElement;
import java.util.Objects;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.observable.Observable;
import me.earth.earthhack.api.setting.SettingContainer;
import me.earth.earthhack.api.setting.event.SettingEvent;
import me.earth.earthhack.api.setting.event.SettingResult;
import me.earth.earthhack.api.util.interfaces.Nameable;

public abstract class Setting<T>
extends Observable<SettingEvent<T>>
implements Jsonable,
Nameable {
    protected final String name;
    protected final T initial;
    protected SettingContainer container;
    protected T value;

    public Setting(String nameIn, T initialValue) {
        this.name = nameIn;
        this.initial = initialValue;
        this.value = initialValue;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public abstract void fromJson(JsonElement var1);

    @Override
    public String toJson() {
        return this.value == null ? "null" : this.value.toString();
    }

    public abstract SettingResult fromString(String var1);

    public abstract String getInputs(String var1);

    public void setValue(T value) {
        this.setValue(value, true);
    }

    public void setValue(T value, boolean withEvent) {
        if (withEvent) {
            SettingEvent<T> event = this.onChange(new SettingEvent<T>(this, value));
            if (!event.isCancelled()) {
                this.value = event.getValue();
            }
        } else {
            this.value = value;
        }
    }

    public T getValue() {
        return this.value;
    }

    public T getInitial() {
        return this.initial;
    }

    public void reset() {
        this.value = this.initial;
    }

    protected void setContainer(SettingContainer container) {
        this.container = container;
    }

    public SettingContainer getContainer() {
        return this.container;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == this.getClass()) {
            Setting o = (Setting)obj;
            return Objects.equals(o.getName(), this.getName()) && Objects.equals(o.getValue(), this.getValue()) && Objects.equals(o.getContainer(), this.getContainer()) && Objects.equals(o.getInitial(), this.getInitial());
        }
        return false;
    }
}
