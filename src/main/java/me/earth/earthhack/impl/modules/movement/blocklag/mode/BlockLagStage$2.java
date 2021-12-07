package me.earth.earthhack.impl.modules.movement.blocklag.mode;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.modules.movement.blocklag.mode.BlockLagStage;

final class BlockLagStage$2
extends BlockLagStage {
    @Override
    public boolean shouldBlockLag(Stage stage) {
        return stage == Stage.POST;
    }
}
