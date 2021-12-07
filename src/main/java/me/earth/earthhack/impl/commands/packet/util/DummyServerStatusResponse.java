/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.ServerStatusResponse
 *  net.minecraft.network.ServerStatusResponse$Players
 *  net.minecraft.network.ServerStatusResponse$Version
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 */
package me.earth.earthhack.impl.commands.packet.util;

import me.earth.earthhack.impl.commands.packet.util.Dummy;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class DummyServerStatusResponse
extends ServerStatusResponse
implements Dummy {
    private static final String FAVICON = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAIAAAAlC+aJAAABhUlEQVR42u3WMUgCUQDG8bc0OFhk\nUBE1KRFZg9LgIMUFBi2JSSlUQyY0VBiElFs0OAlNDQWFYUMFQVDU0BBtNoQRjVFLQdAS1NB2bR+v\n4eCuw3wX38dvetyd93cQRcbEbo9mQD5//9wDq+f6dT2YuddoggEM+IOAu/0oCGnyh8nnun4AwmBy\ngNEzGcAAVQLKOxE4yQ/Bc2UFrooTIL/E2+sGVM5moVxMwP15GuRzBjBAlYAfN1R5lt+HAQxgAAMY\nULMA3WCGf+BMXM8ABjgpgL9CDGAAAxjg/ACr6+9uADsv2uXzwFg0DNX+ghjAALt7OZwEO8/JpkJQ\nyA4AAxigesBCzA8JzQtm7u0LtMNyOgRBrwcYwADVA+TdbMbBzPXTI35YmwuDqNUYwACbS2o+yIz2\ngHxNsLcNCrkIdDS5gAEMcGqAvNhgJxzvTsHqkgatzW4Qqo0BDPjF3K46KOU0eLxchK+PLUiNB4AB\nDPgPAS2NLngoJeE0PwxPF/OwvR4HBjBAkYBveo6/J887s58AAAAASUVORK5CYII=";

    public DummyServerStatusResponse() {
        this.setServerDescription((ITextComponent)new TextComponentString("This is a dummy server!"));
        this.setPlayers(new ServerStatusResponse.Players(0, 0));
        this.setVersion(new ServerStatusResponse.Version("1.12.2", 340));
        this.setFavicon(FAVICON);
    }
}

