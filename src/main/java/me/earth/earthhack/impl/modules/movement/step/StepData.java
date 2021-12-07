package me.earth.earthhack.impl.modules.movement.step;

import me.earth.earthhack.impl.modules.movement.step.Step;
import me.earth.earthhack.impl.util.helpers.render.data.BlockESPModuleData;

final class StepData
extends BlockESPModuleData<Step> {
    public StepData(Step module) {
        super(module);
        this.register(module.height, "Maximum height in blocks that you want to step up.");
        this.register(module.useTimer, "If you want step to use timer.");
        this.register(module.vanilla, "Always to prefer if the server allows it.");
        this.register(module.entityStep, "Step with entities that you are riding on as well.");
        this.register(module.autoOff, "Turn the module off after it stepped.");
        this.register(module.lagTime, "Timeout in milliseconds to wait after we got lagged back by the server.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Allows you to step up blocks.";
    }
}
