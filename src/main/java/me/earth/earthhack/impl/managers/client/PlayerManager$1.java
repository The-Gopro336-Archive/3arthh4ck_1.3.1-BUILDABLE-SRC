package me.earth.earthhack.impl.managers.client;

import me.earth.earthhack.impl.managers.thread.lookup.LookUp;

class PlayerManager$1
extends LookUp {
    PlayerManager$1(LookUp.Type type, String name) {
        super(type, name);
    }

    @Override
    public void onSuccess() {
        PlayerManager.this.add(this.name, this.uuid);
    }

    @Override
    public void onFailure() {
    }
}
