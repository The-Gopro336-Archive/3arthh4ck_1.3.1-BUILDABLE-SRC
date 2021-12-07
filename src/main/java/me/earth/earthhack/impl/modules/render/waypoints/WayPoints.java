package me.earth.earthhack.impl.modules.render.waypoints;

import java.awt.Color;
import java.util.Set;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.EnumHelper;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.modules.render.waypoints.ListenerRender;
import me.earth.earthhack.impl.modules.render.waypoints.WayPointSetting;
import me.earth.earthhack.impl.modules.render.waypoints.mode.WayPointRender;
import me.earth.earthhack.impl.modules.render.waypoints.mode.WayPointType;
import me.earth.earthhack.impl.util.helpers.addable.RegisteringModule;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.util.math.BlockPos;

public class WayPoints
extends RegisteringModule<BlockPos, WayPointSetting> {
    protected final Setting<WayPointRender> render = this.register(new EnumSetting<WayPointRender>("Render", WayPointRender.None));
    protected final Setting<Boolean> ovwInNether = this.register(new BooleanSetting("OVW-Nether", false));
    protected final Setting<Boolean> netherOvw = this.register(new BooleanSetting("Nether-OVW", false));
    protected final Setting<Color> ovwColor = this.register(new ColorSetting("OVW-Color", new Color(0, 255, 0)));
    protected final Setting<Color> netherColor = this.register(new ColorSetting("Nether-Color", new Color(255, 255, 0)));
    protected final Setting<Color> endColor = this.register(new ColorSetting("End-Color", new Color(0, 255, 255)));
    protected final Setting<Float> scale = this.register(new NumberSetting<Float>("Scale", Float.valueOf(0.003f), Float.valueOf(0.001f), Float.valueOf(0.01f)));
    protected final Setting<Float> lineWidth = this.register(new NumberSetting<Float>("LineWidth", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    protected final Setting<Integer> range = this.register(new NumberSetting<Integer>("Range", 1000, 0, Integer.MAX_VALUE));

    public WayPoints() {
        super("Waypoints", Category.Render, "Add Waypoint", "name> <type> <x> <y> <z", s -> new WayPointSetting((String)s, BlockPos.ORIGIN), s -> "A waypoint.");
        this.listeners.add(new ListenerRender(this));
    }

    @Override
    protected PossibleInputs getInput(String input, String[] args) {
        if (args.length < 2) {
            PossibleInputs inputs = super.getInput(input, args);
            if (args.length == 1 && "del".startsWith(args[0].toLowerCase())) {
                inputs.setRest(" <name>");
            }
            return inputs;
        }
        PossibleInputs inputs = PossibleInputs.empty();
        if (args[0].equalsIgnoreCase("del")) {
            if (args.length > 2) {
                return inputs;
            }
            return inputs.setCompletion(this.getInput(input.substring(4), false));
        }
        if (!args[0].equalsIgnoreCase("add")) {
            return inputs;
        }
        switch (args.length) {
            case 2: {
                return inputs.setRest(" <type> <x> <y> <z>");
            }
            case 3: {
                Enum<?> entry = EnumHelper.getEnumStartingWith(args[2], WayPointType.class);
                if (entry == null || entry == WayPointType.None) {
                    return inputs.setRest("\u00a7c unrecognized type!");
                }
                return inputs.setCompletion(TextUtil.substring(entry.toString(), args[2].length())).setRest(" <x> <y> <z>");
            }
            case 4: {
                return inputs.setRest(" <y> <z>");
            }
            case 5: {
                return inputs.setRest(" <z>");
            }
        }
        return inputs;
    }

    @Override
    public void add(String string) {
        double z;
        double y;
        double x;
        if (string == null || string.isEmpty()) {
            ChatUtil.sendMessage("\u00a7cNo WayPoint given!");
            return;
        }
        String[] args = CommandUtil.toArgs(string);
        if (args.length != 5) {
            ChatUtil.sendMessage("\u00a7cExpected 5 arguments: (Name, Type, X, Y, Z), but found " + args.length + "!");
            return;
        }
        WayPointType type = WayPointType.fromString(args[1]);
        if (type == WayPointType.None) {
            ChatUtil.sendMessage("\u00a7cCan't recognize type \u00a7f" + args[1] + "\u00a7c" + "! Try OVW, End or Nether!");
            return;
        }
        try {
            x = Double.parseDouble(args[2]);
            y = Double.parseDouble(args[3]);
            z = Double.parseDouble(args[4]);
        }
        catch (Exception e) {
            ChatUtil.sendMessage("\u00a7cCouldn't parse input to X,Y,Z Coordinates!");
            return;
        }
        WayPointSetting setting = (WayPointSetting)this.addSetting(args[0]);
        if (setting != null) {
            setting.setType(type);
            setting.setValue(new BlockPos(x, y, z));
            ChatUtil.sendMessage("\u00a7aAdded new waypoint: \u00a7f" + args[0] + "\u00a7a" + ".");
        } else {
            ChatUtil.sendMessage("\u00a7cA Waypoint called \u00a7f" + args[0] + "\u00a7c" + " already exists!");
        }
    }

    protected Color getColor(WayPointType type) {
        switch (type) {
            case OVW: {
                return this.ovwColor.getValue();
            }
            case End: {
                return this.endColor.getValue();
            }
            case Nether: {
                return this.netherColor.getValue();
            }
        }
        return Color.WHITE;
    }

    protected Set<WayPointSetting> getWayPoints() {
        return this.added;
    }
}
