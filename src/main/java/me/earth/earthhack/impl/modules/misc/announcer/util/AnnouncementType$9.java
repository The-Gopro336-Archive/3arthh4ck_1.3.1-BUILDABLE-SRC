package me.earth.earthhack.impl.modules.misc.announcer.util;

import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;

final class AnnouncementType$9
extends AnnouncementType {
    @Override
    public String getDefaultMessage() {
        return "Nice shot <NAME>, try hitting next time!";
    }

    @Override
    public String getFile() {
        return "earthhack/util/Announcer_Miss.txt";
    }
}
