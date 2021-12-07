package me.earth.earthhack.impl.commands.packet;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.PacketCommand;
import me.earth.earthhack.impl.commands.packet.arguments.AdvancementArgument;
import me.earth.earthhack.impl.commands.packet.arguments.AdvancementProgressArgument;
import me.earth.earthhack.impl.commands.packet.arguments.AttributeArgument;
import me.earth.earthhack.impl.commands.packet.arguments.BlockArgument;
import me.earth.earthhack.impl.commands.packet.arguments.BlockPosArgument;
import me.earth.earthhack.impl.commands.packet.arguments.BooleanArgument;
import me.earth.earthhack.impl.commands.packet.arguments.BossInfoArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ByteArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ChunkArgument;
import me.earth.earthhack.impl.commands.packet.arguments.CollectionArgument;
import me.earth.earthhack.impl.commands.packet.arguments.CombatTrackerArgument;
import me.earth.earthhack.impl.commands.packet.arguments.DoubleArgument;
import me.earth.earthhack.impl.commands.packet.arguments.EntityArgument;
import me.earth.earthhack.impl.commands.packet.arguments.EntityDataMangerArgument;
import me.earth.earthhack.impl.commands.packet.arguments.EntityLivingBaseArgument;
import me.earth.earthhack.impl.commands.packet.arguments.EntityPaintingArgument;
import me.earth.earthhack.impl.commands.packet.arguments.EntityPlayerArgument;
import me.earth.earthhack.impl.commands.packet.arguments.EntityXPOrbArgument;
import me.earth.earthhack.impl.commands.packet.arguments.EnumArgument;
import me.earth.earthhack.impl.commands.packet.arguments.FloatArgument;
import me.earth.earthhack.impl.commands.packet.arguments.GameProfileArgument;
import me.earth.earthhack.impl.commands.packet.arguments.IntArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ItemArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ItemStackArgument;
import me.earth.earthhack.impl.commands.packet.arguments.IterableArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ListArgument;
import me.earth.earthhack.impl.commands.packet.arguments.LongArgument;
import me.earth.earthhack.impl.commands.packet.arguments.MapArgument;
import me.earth.earthhack.impl.commands.packet.arguments.MapDecorationArgument;
import me.earth.earthhack.impl.commands.packet.arguments.NBTTagCompoundArgument;
import me.earth.earthhack.impl.commands.packet.arguments.NonNullListArgument;
import me.earth.earthhack.impl.commands.packet.arguments.PacketBufferArgument;
import me.earth.earthhack.impl.commands.packet.arguments.PlayerCapabilitiesArgument;
import me.earth.earthhack.impl.commands.packet.arguments.PotionArgument;
import me.earth.earthhack.impl.commands.packet.arguments.PotionEffectArgument;
import me.earth.earthhack.impl.commands.packet.arguments.PublicKeyArgument;
import me.earth.earthhack.impl.commands.packet.arguments.RecipeArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ResourceLocationArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ScoreArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ScoreObjectiveArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ScorePlayerTeamArgument;
import me.earth.earthhack.impl.commands.packet.arguments.SecretKeyArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ServerStatusResponseArgument;
import me.earth.earthhack.impl.commands.packet.arguments.SetArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ShortArgument;
import me.earth.earthhack.impl.commands.packet.arguments.SoundEventArgument;
import me.earth.earthhack.impl.commands.packet.arguments.StatBaseArgument;
import me.earth.earthhack.impl.commands.packet.arguments.StringArgument;
import me.earth.earthhack.impl.commands.packet.arguments.TextComponentArgument;
import me.earth.earthhack.impl.commands.packet.arguments.UUIDArgument;
import me.earth.earthhack.impl.commands.packet.arguments.Vec3dArgument;
import me.earth.earthhack.impl.commands.packet.arguments.WorldArgument;
import me.earth.earthhack.impl.commands.packet.arguments.WorldBorderArgument;
import me.earth.earthhack.impl.commands.packet.arguments.WorldTypeArgument;
import me.earth.earthhack.impl.commands.packet.array.ByteArrayArgument;
import me.earth.earthhack.impl.commands.packet.array.FunctionArrayArgument;
import me.earth.earthhack.impl.commands.packet.array.IntArrayArgument;
import me.earth.earthhack.impl.commands.packet.array.ShortArrayArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.packet.factory.DefaultFactory;
import me.earth.earthhack.impl.commands.packet.factory.PacketFactory;
import me.earth.earthhack.impl.commands.packet.factory.playerlistheaderfooter.SPacketPlayerListHeaderFooterFactory;
import me.earth.earthhack.impl.commands.packet.factory.playerlistitem.SPacketPlayerListItemFactory;
import me.earth.earthhack.impl.commands.packet.generic.GenericArgument;
import me.earth.earthhack.impl.commands.packet.generic.GenericCollectionArgument;
import me.earth.earthhack.impl.commands.packet.generic.GenericListArgument;
import me.earth.earthhack.impl.commands.packet.generic.GenericMapArgument;
import me.earth.earthhack.impl.commands.packet.generic.GenericNonNullListArgument;
import me.earth.earthhack.impl.commands.packet.generic.GenericSetArgument;
import me.earth.earthhack.impl.commands.packet.util.BufferUtil;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.util.mcp.MappingProvider;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.stats.StatBase;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapDecoration;

public class PacketCommandImpl
extends Command
implements Globals,
PacketCommand {
    private final Map<Class<? extends Packet<?>>, List<GenericArgument<?>>> generics;
    private final Map<Class<? extends Packet<?>>, PacketFactory> custom;
    private final Set<Class<? extends Packet<?>>> packets;
    private final Map<Class<?>, PacketArgument<?>> arguments;
    private final PacketFactory default_factory;

    public PacketCommandImpl() {
        super(new String[][]{{"packet"}, {"packet"}, {"index"}, {"arguments"}});
        CommandDescriptions.register(this, "Send/receive packets.");
        this.custom = new HashMap();
        this.generics = new HashMap();
        this.arguments = new HashMap();
        this.packets = new HashSet();
        this.default_factory = new DefaultFactory(this);
        this.setup();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute(String[] args) {
        Packet<?> packet;
        if (args == null || args.length == 1) {
            ChatUtil.sendMessage("<PacketCommand> Use this command to send/receive a Packet. Remember to maybe escape your arguments with \", Arrays and Collection arguments are seperated by ], Map.Entries by ). This command should only be used if you know what you are doing!");
            return;
        }
        if (PacketCommandImpl.mc.player == null || PacketCommandImpl.mc.world == null) {
            ChatUtil.sendMessage("\u00a7cThis command can only be used while ingame!");
            return;
        }
        Class<Packet<?>> packetType = this.getPacket(args[1]);
        if (packetType == null) {
            ChatUtil.sendMessage("\u00a7cCouldn't find packet: \u00a7f" + args[1] + "\u00a7c" + "!");
            return;
        }
        Type type = this.getNetHandlerType(packetType);
        if (type != INetHandlerPlayClient.class && type != INetHandlerPlayServer.class) {
            ChatUtil.sendMessage("\u00a7cPacket \u00a7f" + packetType.getName() + "\u00a7c" + " has unknown NetHandler type: " + "\u00a7f" + type + "\u00a7c" + "!");
            return;
        }
        if (args.length == 2) {
            ChatUtil.sendMessage("\u00a7cPlease specify a constructor index!");
            return;
        }
        PacketFactory gen = this.custom.getOrDefault(packetType, this.default_factory);
        try {
            packet = gen.create(packetType, args);
        }
        catch (ArgParseException e) {
            ChatUtil.sendMessage("\u00a7c" + e.getMessage());
            return;
        }
        if (packet == null) {
            ChatUtil.sendMessage("\u00a7cPacket for \u00a7f" + MappingProvider.simpleName(packetType) + "\u00a7c" + " was null?!");
            return;
        }
        if (type == INetHandlerPlayServer.class) {
            ChatUtil.sendMessage("\u00a7aSending packet \u00a7f" + MappingProvider.simpleName(packetType) + "\u00a7a" + " to server!");
            try {
                PacketCommandImpl.mc.player.connection.sendPacket(packet);
            }
            catch (Throwable t) {
                ChatUtil.sendMessage("\u00a7cAn error occurred while sending packet \u00a7f" + MappingProvider.simpleName(packetType) + "\u00a7c" + ": " + t.getMessage());
                t.printStackTrace();
            }
        } else {
            ChatUtil.sendMessage("\u00a7aAttempting to receive packet \u00a7f" + MappingProvider.simpleName(packetType) + "\u00a7a" + "!");
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            List<Object> rs = BufferUtil.saveReleasableFields(packet);
            try {
                packet.writePacketData(buffer);
                packet.readPacketData(buffer);
                if (!NetworkUtil.receive(packet)) {
                    ChatUtil.sendMessage("\u00a7cThe packet \u00a7f" + MappingProvider.simpleName(packetType) + "\u00a7c" + " got cancelled!");
                }
            }
            catch (Throwable t) {
                ChatUtil.sendMessage("\u00a7cAn error occurred while receiving packet \u00a7f" + MappingProvider.simpleName(packetType) + "\u00a7c" + ": " + t.getMessage());
                t.printStackTrace();
            }
            finally {
                BufferUtil.release(rs);
                BufferUtil.releaseFields(packet);
                BufferUtil.releaseBuffer(buffer);
            }
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        PossibleInputs inputs = PossibleInputs.empty();
        if (PacketCommandImpl.mc.world == null || PacketCommandImpl.mc.player == null) {
            return inputs.setRest("\u00a7c <This command can only be used while ingame!>");
        }
        if (args.length <= 1 || args[1].isEmpty()) {
            return super.getPossibleInputs(args);
        }
        Class<? extends Packet<?>> packet = this.getPacket(args[1]);
        if (packet == null) {
            return inputs.setRest("\u00a7c not found!");
        }
        PacketFactory factory = this.custom.getOrDefault(packet, this.default_factory);
        return factory.getInputs(packet, args);
    }

    @Override
    public Completer onTabComplete(Completer completer) {
        String[] args = completer.getArgs();
        if (args != null && args.length >= 2) {
            Class<? extends Packet<?>> p = this.getPacket(args[1]);
            PacketFactory factory = this.custom.getOrDefault(p, this.default_factory);
            switch (factory.onTabComplete(completer)) {
                case PASS: {
                    break;
                }
                case RETURN: {
                    return completer;
                }
                case SUPER: {
                    return super.onTabComplete(completer);
                }
            }
        }
        if (completer.isSame()) {
            return completer;
        }
        return super.onTabComplete(completer);
    }

    public void addGeneric(Class<? extends Packet<?>> type, GenericArgument<?> argument) {
        this.generics.computeIfAbsent(type, v -> new ArrayList()).add(argument);
    }

    public <T extends Packet<?>> void addCustom(Class<T> type, PacketFactory factory) {
        this.custom.put(type, factory);
    }

    public <T> void addArgument(Class<T> type, PacketArgument<T> argument) {
        this.arguments.put(type, argument);
    }

    public void addPacket(Class<? extends Packet<?>> packet) {
        this.packets.add(packet);
    }

    @Override
    public Class<? extends Packet<?>> getPacket(String name) {
        for (Class<? extends Packet<?>> clazz : this.packets) {
            if (!TextUtil.startsWith(this.getName(clazz), name)) continue;
            return clazz;
        }
        return null;
    }

    @Override
    public Map<Class<? extends Packet<?>>, List<GenericArgument<?>>> getGenerics() {
        return this.generics;
    }

    @Override
    public Map<Class<? extends Packet<?>>, PacketFactory> getCustom() {
        return this.custom;
    }

    @Override
    public Set<Class<? extends Packet<?>>> getPackets() {
        return this.packets;
    }

    @Override
    public Map<Class<?>, PacketArgument<?>> getArguments() {
        return this.arguments;
    }

    @Override
    public String getName(Class<? extends Packet<?>> packet) {
        String simpleName = MappingProvider.simpleName(packet);
        if (packet.getSuperclass() != Object.class) {
            simpleName = MappingProvider.simpleName(packet.getSuperclass()) + "-" + simpleName;
        }
        return simpleName;
    }

    private void setup() {
        this.addCustom(SPacketPlayerListHeaderFooter.class, new SPacketPlayerListHeaderFooterFactory(this));
        this.addCustom(SPacketPlayerListItem.class, new SPacketPlayerListItemFactory(this));
        this.addArgument(Boolean.TYPE, new BooleanArgument());
        this.addArgument(Integer.TYPE, new IntArgument());
        this.addArgument(Float.TYPE, new FloatArgument());
        this.addArgument(Short.TYPE, new ShortArgument());
        this.addArgument(Long.TYPE, new LongArgument());
        this.addArgument(Double.TYPE, new DoubleArgument());
        this.addArgument(Byte.TYPE, new ByteArgument());
        this.addArgument(String.class, new StringArgument());
        this.addArgument(BlockPos.class, new BlockPosArgument());
        this.addArgument(Vec3d.class, new Vec3dArgument());
        this.addArgument(Chunk.class, new ChunkArgument());
        this.addArgument(UUID.class, new UUIDArgument());
        this.addArgument(GameProfile.class, new GameProfileArgument());
        this.addArgument(ResourceLocation.class, new ResourceLocationArgument());
        this.addArgument(NBTTagCompound.class, new NBTTagCompoundArgument());
        this.addArgument(World.class, new WorldArgument());
        this.addArgument(ITextComponent.class, new TextComponentArgument());
        this.addArgument(PacketBuffer.class, new PacketBufferArgument());
        this.addArgument(Item.class, new ItemArgument());
        this.addArgument(ItemStack.class, new ItemStackArgument());
        this.addArgument(Block.class, new BlockArgument());
        this.addArgument(Potion.class, new PotionArgument());
        this.addArgument(PotionEffect.class, new PotionEffectArgument());
        this.addArgument(WorldType.class, new WorldTypeArgument());
        this.addArgument(SoundEvent.class, new SoundEventArgument());
        this.addArgument(PlayerCapabilities.class, new PlayerCapabilitiesArgument());
        this.addArgument(IRecipe.class, new RecipeArgument());
        this.addArgument(Entity.class, new EntityArgument());
        this.addArgument(EntityXPOrb.class, new EntityXPOrbArgument());
        this.addArgument(EntityPlayer.class, new EntityPlayerArgument());
        this.addArgument(EntityPainting.class, new EntityPaintingArgument());
        this.addArgument(EntityLivingBase.class, new EntityLivingBaseArgument());
        this.addArgument(IAttributeInstance.class, new AttributeArgument());
        this.addArgument(SPacketPlayerListItem.Action.class, new EnumArgument<SPacketPlayerListItem.Action>(SPacketPlayerListItem.Action.class));
        this.addArgument(SPacketWorldBorder.Action.class, new EnumArgument<SPacketWorldBorder.Action>(SPacketWorldBorder.Action.class));
        this.addArgument(SPacketUpdateBossInfo.Operation.class, new EnumArgument<SPacketUpdateBossInfo.Operation>(SPacketUpdateBossInfo.Operation.class));
        this.addArgument(SPacketCombatEvent.Event.class, new EnumArgument<SPacketCombatEvent.Event>(SPacketCombatEvent.Event.class));
        this.addArgument(SPacketRecipeBook.State.class, new EnumArgument<SPacketRecipeBook.State>(SPacketRecipeBook.State.class));
        this.addArgument(SPacketTitle.Type.class, new EnumArgument<SPacketTitle.Type>(SPacketTitle.Type.class));
        this.addArgument(EntityEquipmentSlot.class, new EnumArgument<EntityEquipmentSlot>(EntityEquipmentSlot.class));
        this.addArgument(EnumDifficulty.class, new EnumArgument<EnumDifficulty>(EnumDifficulty.class));
        this.addArgument(EnumParticleTypes.class, new EnumArgument<EnumParticleTypes>(EnumParticleTypes.class));
        this.addArgument(SoundCategory.class, new EnumArgument<SoundCategory>(SoundCategory.class));
        this.addArgument(EnumConnectionState.class, new EnumArgument<EnumConnectionState>(EnumConnectionState.class));
        this.addArgument(CPacketClientStatus.State.class, new EnumArgument<CPacketClientStatus.State>(CPacketClientStatus.State.class));
        this.addArgument(CPacketEntityAction.Action.class, new EnumArgument<CPacketEntityAction.Action>(CPacketEntityAction.Action.class));
        this.addArgument(CPacketPlayerDigging.Action.class, new EnumArgument<CPacketPlayerDigging.Action>(CPacketPlayerDigging.Action.class));
        this.addArgument(CPacketResourcePackStatus.Action.class, new EnumArgument<CPacketResourcePackStatus.Action>(CPacketResourcePackStatus.Action.class));
        this.addArgument(CPacketSeenAdvancements.Action.class, new EnumArgument<CPacketSeenAdvancements.Action>(CPacketSeenAdvancements.Action.class));
        this.addArgument(EnumFacing.class, new EnumArgument<EnumFacing>(EnumFacing.class));
        this.addArgument(ClickType.class, new EnumArgument<ClickType>(ClickType.class));
        this.addArgument(EnumHandSide.class, new EnumArgument<EnumHandSide>(EnumHandSide.class));
        this.addArgument(EntityPlayer.EnumChatVisibility.class, new EnumArgument<EntityPlayer.EnumChatVisibility>(EntityPlayer.EnumChatVisibility.class));
        this.addArgument(EnumHand.class, new EnumArgument<EnumHand>(EnumHand.class));
        this.addArgument(ChatType.class, new EnumArgument<ChatType>(ChatType.class));
        this.addArgument(GameType.class, new EnumArgument<GameType>(GameType.class));
        this.addArgument(MapDecoration.class, new MapDecorationArgument());
        this.addArgument(Advancement.class, new AdvancementArgument());
        this.addArgument(NonNullList.class, new NonNullListArgument());
        this.addArgument(Map.class, new MapArgument());
        this.addArgument(List.class, new ListArgument());
        this.addArgument(Collection.class, new CollectionArgument());
        this.addArgument(Set.class, new SetArgument());
        this.addArgument(Iterable.class, new IterableArgument());
        this.addArgument(SecretKey.class, new SecretKeyArgument());
        this.addArgument(PublicKey.class, new PublicKeyArgument());
        this.addArgument(WorldBorder.class, new WorldBorderArgument());
        this.addArgument(BossInfo.class, new BossInfoArgument());
        this.addArgument(ScoreObjective.class, new ScoreObjectiveArgument());
        this.addArgument(CombatTracker.class, new CombatTrackerArgument());
        this.addArgument(EntityDataManager.class, new EntityDataMangerArgument());
        this.addArgument(Score.class, new ScoreArgument());
        this.addArgument(ScorePlayerTeam.class, new ScorePlayerTeamArgument());
        this.addArgument(ServerStatusResponse.class, new ServerStatusResponseArgument());
        this.arguments.put(int[].class, new IntArrayArgument());
        this.arguments.put(byte[].class, new ByteArrayArgument());
        this.arguments.put(short[].class, new ShortArrayArgument());
        this.arguments.put(String[].class, new FunctionArrayArgument<String>(String[].class, this.getArgument(String.class), String[]::new));
        this.arguments.put(ITextComponent[].class, new FunctionArrayArgument<ITextComponent>(ITextComponent[].class, this.getArgument(ITextComponent.class), ITextComponent[]::new));
        try {
            Constructor recipe = SPacketRecipeBook.class.getDeclaredConstructor(SPacketRecipeBook.State.class, List.class, List.class, Boolean.TYPE, Boolean.TYPE);
            this.addGeneric(SPacketRecipeBook.class, new GenericListArgument(recipe, 1, this.arguments.get(IRecipe.class)));
            this.addGeneric(SPacketRecipeBook.class, new GenericListArgument(recipe, 2, this.arguments.get(IRecipe.class)));
            Constructor teams = SPacketTeams.class.getDeclaredConstructor(ScorePlayerTeam.class, Collection.class, Integer.TYPE);
            this.addGeneric(SPacketTeams.class, new GenericCollectionArgument(teams, 1, this.arguments.get(String.class)));
            Constructor advancement = SPacketAdvancementInfo.class.getDeclaredConstructor(Boolean.TYPE, Collection.class, Set.class, Map.class);
            this.addGeneric(SPacketAdvancementInfo.class, new GenericCollectionArgument(advancement, 1, this.arguments.get(Advancement.class)));
            this.addGeneric(SPacketAdvancementInfo.class, new GenericSetArgument(advancement, 2, this.arguments.get(ResourceLocation.class)));
            this.addGeneric(SPacketAdvancementInfo.class, new GenericMapArgument<ResourceLocation, AdvancementProgress, HashMap>(HashMap.class, advancement, 3, new ResourceLocationArgument(), new AdvancementProgressArgument()));
            Constructor properties = SPacketEntityProperties.class.getDeclaredConstructor(Integer.TYPE, Collection.class);
            this.addGeneric(SPacketEntityProperties.class, new GenericCollectionArgument(properties, 1, this.arguments.get(IAttributeInstance.class)));
            Constructor constructor = SPacketExplosion.class.getDeclaredConstructor(Double.TYPE, Double.TYPE, Double.TYPE, Float.TYPE, List.class, Vec3d.class);
            this.addGeneric(SPacketExplosion.class, new GenericListArgument(constructor, 4, this.arguments.get(BlockPos.class)));
            Constructor posLook = SPacketPlayerPosLook.class.getDeclaredConstructor(Double.TYPE, Double.TYPE, Double.TYPE, Float.TYPE, Float.TYPE, Set.class, Integer.TYPE);
            this.addGeneric(SPacketPlayerPosLook.class, new GenericSetArgument<SPacketPlayerPosLook.EnumFlags>(posLook, 5, new EnumArgument<SPacketPlayerPosLook.EnumFlags>(SPacketPlayerPosLook.EnumFlags.class)));
            Constructor windowItems = SPacketWindowItems.class.getDeclaredConstructor(Integer.TYPE, NonNullList.class);
            this.addGeneric(SPacketWindowItems.class, new GenericNonNullListArgument(windowItems, 1, this.arguments.get(ItemStack.class)));
            Constructor maps = SPacketMaps.class.getDeclaredConstructor(Integer.TYPE, Byte.TYPE, Boolean.TYPE, Collection.class, byte[].class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            this.addGeneric(SPacketMaps.class, new GenericCollectionArgument(maps, 3, this.arguments.get(MapDecoration.class)));
            Constructor statistics = SPacketStatistics.class.getDeclaredConstructor(Map.class);
            this.addGeneric(SPacketStatistics.class, new GenericMapArgument<StatBase, Integer, HashMap>(HashMap.class, statistics, 0, new StatBaseArgument(), new IntArgument()));
        }
        catch (NoSuchMethodException e) {
            throw new IllegalStateException("Constructor of a packet missing: " + e.getMessage());
        }
        for (Class<Packet<?>> packet : PacketUtil.getAllPackets()) {
            Type netHandler = this.getNetHandlerType(packet);
            if (netHandler != INetHandlerPlayClient.class && netHandler != INetHandlerPlayServer.class) continue;
            if (!this.custom.containsKey(packet)) {
                for (Constructor<?> ctr : packet.getDeclaredConstructors()) {
                    for (Class<?> type : ctr.getParameterTypes()) {
                        if (this.arguments.containsKey(type)) continue;
                        Earthhack.getLogger().error("<PacketCommand> No Argument found for: " + type.getName() + " in " + packet.getName());
                    }
                }
            }
            for (Class clazz : this.packets) {
                if (!clazz.getSimpleName().equals(packet.getSimpleName()) || clazz.equals(packet)) continue;
                Earthhack.getLogger().warn(clazz.getName() + " SimpleName clashes with: " + packet.getName());
            }
            this.addPacket(packet);
        }
    }

    private <T> PacketArgument<T> getArgument(Class<T> clazz) {
        return this.arguments.get(clazz);
    }

    private Type getNetHandlerType(Class<? extends Packet<?>> packet) {
        Class<Packet<?>> clazz = packet;
        do {
            Type[] types;
            for (Type genericInterface : types = clazz.getGenericInterfaces()) {
                Type[] typeArray;
                int n;
                int n2;
                if (!(genericInterface instanceof ParameterizedType) || ((ParameterizedType)genericInterface).getRawType() != Packet.class || (n2 = 0) >= (n = (typeArray = ((ParameterizedType)genericInterface).getActualTypeArguments()).length)) continue;
                Type type = typeArray[n2];
                return type;
            }
        } while ((clazz = clazz.getSuperclass()) != Object.class);
        return null;
    }
}
