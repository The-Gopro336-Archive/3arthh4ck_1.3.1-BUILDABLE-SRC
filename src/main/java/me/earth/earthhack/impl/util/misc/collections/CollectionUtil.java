package me.earth.earthhack.impl.util.misc.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class CollectionUtil {
    public static void emptyQueue(Queue<Runnable> runnables) {
        CollectionUtil.emptyQueue(runnables, Runnable::run);
    }

    public static <T> void emptyQueue(Queue<T> queue, Consumer<T> onPoll) {
        while (!queue.isEmpty()) {
            T polled = queue.poll();
            if (polled == null) continue;
            onPoll.accept(polled);
        }
    }

    @SafeVarargs
    public static <T> List<List<T>> split(List<T> list, Predicate<T> ... predicates) {
        ArrayList<List<T>> result = new ArrayList<List<T>>(predicates.length + 1);
        ArrayList<T> current = new ArrayList<T>(list);
        ArrayList next = new ArrayList();
        for (Predicate predicate : predicates) {
            Iterator it = current.iterator();
            while (it.hasNext()) {
                Object t = it.next();
                if (!predicate.test(t)) continue;
                next.add(t);
                it.remove();
            }
            result.add(next);
            next = new ArrayList();
        }
        result.add(current);
        return result;
    }

    public static <T, C extends T> C getByClass(Class<C> clazz, Collection<T> collection) {
        for (T t : collection) {
            if (!clazz.isInstance(t)) continue;
            return (C)t;
        }
        return null;
    }

    public static <T, R> List<T> convert(R[] array, Function<R, T> function) {
        ArrayList<T> result = new ArrayList<T>(array.length);
        for (int i = 0; i < array.length; ++i) {
            result.add(i, function.apply(array[i]));
        }
        return result;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        ArrayList<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        LinkedHashMap result = new LinkedHashMap();
        for (Map.Entry entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static <T> T getLast(Collection<T> iterable) {
        T last = null;
        for (T t : iterable) {
            last = t;
        }
        return last;
    }
}
