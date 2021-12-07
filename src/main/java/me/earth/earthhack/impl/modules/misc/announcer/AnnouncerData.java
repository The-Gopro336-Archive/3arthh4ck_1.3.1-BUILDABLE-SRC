package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.misc.announcer.Announcer;

final class AnnouncerData
extends DefaultData<Announcer> {
    public AnnouncerData(Announcer module) {
        super(module);
        this.register(module.delay, "Interval in seconds that messages will be send with. Some Antispams might kick you for low delays.");
        this.register(module.distance, "Announces the distance you travelled.");
        this.register(module.mine, "Announces the blocks you just mined.");
        this.register(module.place, "Announces the blocks you've placed.");
        this.register(module.eat, "Announces the foot you eat.");
        this.register(module.join, "Greets players that join the server.");
        this.register(module.leave, "Says bye to players that leave the server.");
        this.register(module.totems, "Announces totem pops.");
        this.register(module.autoEZ, "Announces deaths.");
        this.register(module.miss, "Announces when someone misses a shot with a bow.");
        this.register(module.friends, "Announce totem pops of friends.");
        this.register(module.antiKick, "Appends a random suffix to trick antispams.");
        this.register(module.green, "Prepends a \">\".");
        this.register(module.refresh, "Refreshes the files in the earthhack/util folder.");
        this.register(module.random, "Works if your files in the earthhack/util folder have multiple lines. Then one of those gets selected randomly instead of the first line.");
        this.register(module.minDist, "The minimal travelled distance to announce.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "When you want to be really annoying. You'll find configurable files in the earthhack/util folder.";
    }
}
