package me.earth.earthhack.api.event.bus.api;

public interface ICancellable {
    public void setCancelled(boolean var1);

    public boolean isCancelled();
}
