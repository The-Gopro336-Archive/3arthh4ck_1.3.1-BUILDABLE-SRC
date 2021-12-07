/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.misc.intintmap;

import me.earth.earthhack.impl.util.misc.intintmap.IntIntMap;
import me.earth.earthhack.impl.util.misc.intintmap.Tools;

public class IntIntMapImpl
implements IntIntMap {
    private static final int FREE_KEY = 0;
    public static final int NO_VALUE = 0;
    private int[] m_data;
    private boolean m_hasFreeKey;
    private int m_freeValue;
    private final float m_fillFactor;
    private int m_threshold;
    private int m_size;
    private int m_mask;
    private int m_mask2;

    public IntIntMapImpl(int size, float fillFactor) {
        if (fillFactor <= 0.0f || fillFactor >= 1.0f) {
            throw new IllegalArgumentException("FillFactor must be in (0, 1)");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive!");
        }
        int capacity = Tools.arraySize(size, fillFactor);
        this.m_mask = capacity - 1;
        this.m_mask2 = capacity * 2 - 1;
        this.m_fillFactor = fillFactor;
        this.m_data = new int[capacity * 2];
        this.m_threshold = (int)((float)capacity * fillFactor);
    }

    @Override
    public int get(int key) {
        int ptr = (Tools.phiMix(key) & this.m_mask) << 1;
        if (key == 0) {
            return this.m_hasFreeKey ? this.m_freeValue : 0;
        }
        int k = this.m_data[ptr];
        if (k == 0) {
            return 0;
        }
        if (k == key) {
            return this.m_data[ptr + 1];
        }
        do {
            if ((k = this.m_data[ptr = ptr + 2 & this.m_mask2]) != 0) continue;
            return 0;
        } while (k != key);
        return this.m_data[ptr + 1];
    }

    @Override
    public int put(int key, int value) {
        if (key == 0) {
            int ret = this.m_freeValue;
            if (!this.m_hasFreeKey) {
                ++this.m_size;
            }
            this.m_hasFreeKey = true;
            this.m_freeValue = value;
            return ret;
        }
        int ptr = (Tools.phiMix(key) & this.m_mask) << 1;
        int k = this.m_data[ptr];
        if (k == 0) {
            this.m_data[ptr] = key;
            this.m_data[ptr + 1] = value;
            if (this.m_size >= this.m_threshold) {
                this.rehash(this.m_data.length * 2);
            } else {
                ++this.m_size;
            }
            return 0;
        }
        if (k == key) {
            int ret = this.m_data[ptr + 1];
            this.m_data[ptr + 1] = value;
            return ret;
        }
        do {
            if ((k = this.m_data[ptr = ptr + 2 & this.m_mask2]) != 0) continue;
            this.m_data[ptr] = key;
            this.m_data[ptr + 1] = value;
            if (this.m_size >= this.m_threshold) {
                this.rehash(this.m_data.length * 2);
            } else {
                ++this.m_size;
            }
            return 0;
        } while (k != key);
        int ret = this.m_data[ptr + 1];
        this.m_data[ptr + 1] = value;
        return ret;
    }

    @Override
    public int remove(int key) {
        if (key == 0) {
            if (!this.m_hasFreeKey) {
                return 0;
            }
            this.m_hasFreeKey = false;
            --this.m_size;
            return this.m_freeValue;
        }
        int ptr = (Tools.phiMix(key) & this.m_mask) << 1;
        int k = this.m_data[ptr];
        if (k == key) {
            int res = this.m_data[ptr + 1];
            this.shiftKeys(ptr);
            --this.m_size;
            return res;
        }
        if (k == 0) {
            return 0;
        }
        do {
            if ((k = this.m_data[ptr = ptr + 2 & this.m_mask2]) != key) continue;
            int res = this.m_data[ptr + 1];
            this.shiftKeys(ptr);
            --this.m_size;
            return res;
        } while (k != 0);
        return 0;
    }

    private void shiftKeys(int pos) {
        int[] data = this.m_data;
        while (true) {
            int k;
            int last = pos;
            pos = last + 2 & this.m_mask2;
            while (true) {
                if ((k = data[pos]) == 0) {
                    data[last] = 0;
                    return;
                }
                int slot = (Tools.phiMix(k) & this.m_mask) << 1;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 2 & this.m_mask2;
            }
            data[last] = k;
            data[last + 1] = data[pos + 1];
        }
    }

    @Override
    public int size() {
        return this.m_size;
    }

    private void rehash(int newCapacity) {
        this.m_threshold = (int)((float)(newCapacity / 2) * this.m_fillFactor);
        this.m_mask = newCapacity / 2 - 1;
        this.m_mask2 = newCapacity - 1;
        int oldCapacity = this.m_data.length;
        int[] oldData = this.m_data;
        this.m_data = new int[newCapacity];
        this.m_size = this.m_hasFreeKey ? 1 : 0;
        for (int i = 0; i < oldCapacity; i += 2) {
            int oldKey = oldData[i];
            if (oldKey == 0) continue;
            this.put(oldKey, oldData[i + 1]);
        }
    }
}

