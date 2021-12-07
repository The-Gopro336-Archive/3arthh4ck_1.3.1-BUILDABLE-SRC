package me.earth.earthhack.impl.modules.misc.announcer.util;

import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;

final class AnnouncementType$2
extends AnnouncementType {
    @Override
    public String getDefaultMessage() {
        return "I just mined <NUMBER> <NAME>!";
    }

    @Override
    public String getFile() {
        return "earthhack/util/Announcer_Mine.txt";
    }
}
