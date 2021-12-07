package me.earth.earthhack.api.register;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.api.register.Registrable;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.util.interfaces.Nameable;

public abstract class AbstractRegister<T extends Nameable>
implements Register<T> {
    protected final Map<String, T> registered;

    public AbstractRegister() {
        this(new ConcurrentHashMap());
    }

    public AbstractRegister(Map<String, T> map) {
        this.registered = map;
    }

    @Override
    public void register(T object) throws AlreadyRegisteredException {
        T alreadyRegistered = this.getObject(object.getName());
        if (alreadyRegistered != null) {
            throw new AlreadyRegisteredException((Nameable)object, (Nameable)alreadyRegistered);
        }
        if (object instanceof Registrable) {
            ((Registrable)object).onRegister();
        }
        this.registered.put(object.getName().toLowerCase(), object);
    }

    @Override
    public void unregister(T object) throws CantUnregisterException {
        if (object instanceof Registrable) {
            ((Registrable)object).onUnRegister();
        }
        for (Map.Entry<String, T> entry : this.registered.entrySet()) {
            if (!object.equals(entry.getValue())) continue;
            this.registered.remove(entry.getKey());
        }
    }

    @Override
    public T getObject(String name) {
        return (T)((Nameable)this.registered.get(name.toLowerCase()));
    }

    @Override
    public <C extends T> C getByClass(Class<C> clazz) {
        for (Map.Entry<String, T> entry : this.registered.entrySet()) {
            if (!clazz.isInstance(entry.getValue())) continue;
            return (C)((Nameable)entry.getValue());
        }
        return null;
    }

    @Override
    public Collection<T> getRegistered() {
        return this.registered.values();
    }
}
