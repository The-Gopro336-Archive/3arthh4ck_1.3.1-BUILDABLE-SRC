/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.api.event.bus;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.api.event.bus.api.ICancellable;
import me.earth.earthhack.api.event.bus.api.Listener;
import me.earth.earthhack.api.event.bus.api.Subscriber;

public class SimpleBus
implements EventBus {
    private final Map<Class<?>, List<Listener>> listeners = new ConcurrentHashMap();
    private final Set<Subscriber> subscribers = Collections.newSetFromMap(new ConcurrentHashMap());
    private final Set<Listener> subbedlisteners = Collections.newSetFromMap(new ConcurrentHashMap());

    @Override
    public void post(Object object) {
        List<Listener> listening = this.listeners.get(object.getClass());
        if (listening != null) {
            for (Listener listener : listening) {
                listener.invoke(object);
            }
        }
    }

    @Override
    public void post(Object object, Class<?> type) {
        List<Listener> listening = this.listeners.get(object.getClass());
        if (listening != null) {
            for (Listener listener : listening) {
                if (listener.getType() != null && listener.getType() != type) continue;
                listener.invoke(object);
            }
        }
    }

    @Override
    public boolean postCancellable(ICancellable object) {
        List<Listener> listening = this.listeners.get(object.getClass());
        if (listening != null) {
            for (Listener listener : listening) {
                listener.invoke(object);
                if (!object.isCancelled()) continue;
                return true;
            }
        }
        return object.isCancelled();
    }

    @Override
    public boolean postCancellable(ICancellable object, Class<?> type) {
        List<Listener> listening = this.listeners.get(object.getClass());
        if (listening != null) {
            for (Listener listener : listening) {
                if (listener.getType() != null && listener.getType() != type) continue;
                listener.invoke(object);
                if (!object.isCancelled()) continue;
                return true;
            }
        }
        return object.isCancelled();
    }

    @Override
    public void postReversed(Object object, Class<?> type) {
        List<Listener> list = this.listeners.get(object.getClass());
        if (list != null) {
            ListIterator<Listener> li = list.listIterator(list.size());
            while (li.hasPrevious()) {
                Listener l = li.previous();
                if (l == null || l.getType() != null && l.getType() != type) continue;
                l.invoke(object);
            }
        }
    }

    @Override
    public void subscribe(Object object) {
        if (object instanceof Subscriber) {
            Subscriber subscriber = (Subscriber)object;
            for (Listener<?> listener : subscriber.getListeners()) {
                this.register(listener);
            }
            this.subscribers.add(subscriber);
        }
    }

    @Override
    public void unsubscribe(Object object) {
        if (object instanceof Subscriber) {
            Subscriber subscriber = (Subscriber)object;
            for (Listener<?> listener : subscriber.getListeners()) {
                this.unregister(listener);
            }
            this.subscribers.remove(subscriber);
        }
    }

    @Override
    public void register(Listener<?> listener) {
        if (this.subbedlisteners.add(listener)) {
            this.addAtPriority(listener, this.listeners.computeIfAbsent(listener.getTarget(), v -> new CopyOnWriteArrayList()));
        }
    }

    @Override
    public void unregister(Listener<?> listener) {
        List<Listener> list;
        if (this.subbedlisteners.remove(listener) && (list = this.listeners.get(listener.getTarget())) != null) {
            list.remove(listener);
        }
    }

    @Override
    public boolean isSubscribed(Object object) {
        if (object instanceof Subscriber) {
            return this.subscribers.contains(object);
        }
        if (object instanceof Listener) {
            return this.subbedlisteners.contains(object);
        }
        return false;
    }

    @Override
    public boolean hasSubscribers(Class<?> clazz) {
        List<Listener> listening = this.listeners.get(clazz);
        return listening != null && !listening.isEmpty();
    }

    @Override
    public boolean hasSubscribers(Class<?> clazz, Class<?> type) {
        List<Listener> listening = this.listeners.get(clazz);
        return listening != null && listening.stream().anyMatch(listener -> listener.getType() == null || listener.getType() == type);
    }

    private void addAtPriority(Listener<?> listener, List<Listener> list) {
        int index;
        for (index = 0; index < list.size() && listener.getPriority() < list.get(index).getPriority(); ++index) {
        }
        list.add(index, listener);
    }
}

