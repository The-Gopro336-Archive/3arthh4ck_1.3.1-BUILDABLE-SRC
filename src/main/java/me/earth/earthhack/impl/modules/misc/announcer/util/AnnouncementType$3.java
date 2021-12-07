package me.earth.earthhack.impl.modules.misc.announcer.util;

import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;

final class AnnouncementType$3
extends AnnouncementType {
    @Override
    public String getDefaultMessage() {
        return "I just placed <NUMBER> <NAME>!";
    }

    @Override
    public String getFile() {
        return "earthhack/util/Announcer_Place.txt";
    }
}
