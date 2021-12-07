package me.earth.earthhack.impl.modules.misc.announcer.util;

import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;

final class AnnouncementType$1
extends AnnouncementType {
    @Override
    public String getDefaultMessage() {
        return "I just walked <NUMBER> Blocks!";
    }

    @Override
    public String getFile() {
        return "earthhack/util/Announcer_Distance.txt";
    }
}
