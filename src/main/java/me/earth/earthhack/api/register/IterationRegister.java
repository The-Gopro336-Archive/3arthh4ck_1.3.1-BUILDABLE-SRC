package me.earth.earthhack.api.register;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.api.register.Registrable;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.util.interfaces.Nameable;

public abstract class IterationRegister<T extends Nameable>
implements Register<T> {
    protected final List<T> registered = new ArrayList<T>();

    @Override
    public void register(T object) throws AlreadyRegisteredException {
        T alreadyRegistered = this.getObject(object.getName());
        if (alreadyRegistered != null) {
            throw new AlreadyRegisteredException((Nameable)object, (Nameable)alreadyRegistered);
        }
        this.registered.add(object);
        if (object instanceof Registrable) {
            ((Registrable)object).onRegister();
        }
    }

    @Override
    public void unregister(T object) throws CantUnregisterException {
        if (object instanceof Registrable) {
            ((Registrable)object).onUnRegister();
        }
        this.registered.remove(object);
    }

    @Override
    public T getObject(String name) {
        name = name.toLowerCase();
        for (Nameable t : this.registered) {
            if (!t.getName().equalsIgnoreCase(name)) continue;
            return (T)t;
        }
        return null;
    }

    @Override
    public <C extends T> C getByClass(Class<C> clazz) {
        for (Nameable t : this.registered) {
            if (clazz != t.getClass()) continue;
            return (C)t;
        }
        return null;
    }

    @Override
    public Collection<T> getRegistered() {
        return this.registered;
    }
}
