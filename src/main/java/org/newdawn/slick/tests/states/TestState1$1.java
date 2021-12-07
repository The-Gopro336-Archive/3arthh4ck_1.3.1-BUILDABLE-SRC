package org.newdawn.slick.tests.states;

import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.transition.CrossStateTransition;

class TestState1$1
extends CrossStateTransition {
    final long val$start;

    TestState1$1(GameState x0, long l) {
        this.val$start = l;
        super(x0);
    }

    @Override
    public boolean isComplete() {
        return System.currentTimeMillis() - this.val$start > 2000L;
    }

    @Override
    public void init(GameState firstState, GameState secondState) {
    }
}
