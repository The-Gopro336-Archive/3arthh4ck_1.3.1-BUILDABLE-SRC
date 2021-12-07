package org.newdawn.slick.font;

import java.util.Iterator;
import java.util.ListIterator;

class GlyphPage$1
implements Iterator {
    final ListIterator val$iter;

    GlyphPage$1(ListIterator listIterator) {
        this.val$iter = listIterator;
    }

    @Override
    public boolean hasNext() {
        return this.val$iter.hasPrevious();
    }

    public Object next() {
        return this.val$iter.previous();
    }

    @Override
    public void remove() {
        this.val$iter.remove();
    }
}
