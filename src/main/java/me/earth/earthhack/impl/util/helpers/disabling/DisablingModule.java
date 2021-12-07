package me.earth.earthhack.impl.util.helpers.disabling;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.api.Listener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.client.ShutDownEvent;
import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.util.helpers.disabling.IDisablingModule;

public abstract class DisablingModule
extends Module
implements IDisablingModule {
    public DisablingModule(String name, Category category) {
        super(name, category);
        this.listeners.add(DisablingModule.newDisconnectDisabler(this));
        this.listeners.add(DisablingModule.newDeathDisabler(this));
        this.listeners.add(DisablingModule.newShutDownDisabler(this));
    }

    @Override
    public void onShutDown() {
        this.disable();
    }

    @Override
    public void onDeath() {
        this.disable();
    }

    @Override
    public void onDisconnect() {
        this.disable();
    }

    public static void makeDisablingModule(Module module) {
        module.getListeners().add(DisablingModule.newDisconnectDisabler(module));
        module.getListeners().add(DisablingModule.newDeathDisabler(module));
        module.getListeners().add(DisablingModule.newShutDownDisabler(module));
    }

    public static Listener<?> newDisconnectDisabler(final Module module) {
        if (module instanceof IDisablingModule) {
            final IDisablingModule disabling = (IDisablingModule)((Object)module);
            return new EventListener<DisconnectEvent>(DisconnectEvent.class){

                @Override
                public void invoke(DisconnectEvent event) {
                    Globals.mc.addScheduledTask(disabling::onDisconnect);
                }
            };
        }
        return new EventListener<DisconnectEvent>(DisconnectEvent.class){

            @Override
            public void invoke(DisconnectEvent event) {
                Globals.mc.addScheduledTask(module::disable);
            }
        };
    }

    public static Listener<?> newDeathDisabler(final Module module) {
        if (module instanceof IDisablingModule) {
            final IDisablingModule disabling = (IDisablingModule)((Object)module);
            return new EventListener<DeathEvent>(DeathEvent.class){

                @Override
                public void invoke(DeathEvent event) {
                    if (event.getEntity() != null && event.getEntity().equals(Globals.mc.player)) {
                        Globals.mc.addScheduledTask(disabling::onDeath);
                    }
                }
            };
        }
        return new EventListener<DeathEvent>(DeathEvent.class){

            @Override
            public void invoke(DeathEvent event) {
                if (event.getEntity() != null && event.getEntity().equals(Globals.mc.player)) {
                    Globals.mc.addScheduledTask(module::disable);
                }
            }
        };
    }

    public static Listener<?> newShutDownDisabler(final Module module) {
        if (module instanceof IDisablingModule) {
            final IDisablingModule disabling = (IDisablingModule)((Object)module);
            return new EventListener<ShutDownEvent>(ShutDownEvent.class){

                @Override
                public void invoke(ShutDownEvent event) {
                    Globals.mc.addScheduledTask(disabling::onDisconnect);
                }
            };
        }
        return new EventListener<ShutDownEvent>(ShutDownEvent.class){

            @Override
            public void invoke(ShutDownEvent event) {
                Globals.mc.addScheduledTask(module::disable);
            }
        };
    }
}
