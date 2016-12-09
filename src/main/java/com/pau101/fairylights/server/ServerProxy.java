package com.pau101.fairylights.server;

import java.time.Month;

import javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.block.BlockFastener;
import com.pau101.fairylights.server.block.entity.BlockEntityFastener;
import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.config.Configurator;
import com.pau101.fairylights.server.creativetabs.CreativeTabsFairyLights;
import com.pau101.fairylights.server.entity.EntityFenceFastener;
import com.pau101.fairylights.server.entity.EntityLadder;
import com.pau101.fairylights.server.item.ItemConnectionGarland;
import com.pau101.fairylights.server.item.ItemConnectionHangingLights;
import com.pau101.fairylights.server.item.ItemConnectionLetterBunting;
import com.pau101.fairylights.server.item.ItemConnectionPennantBunting;
import com.pau101.fairylights.server.item.ItemConnectionTinsel;
import com.pau101.fairylights.server.item.ItemLadder;
import com.pau101.fairylights.server.item.ItemLight;
import com.pau101.fairylights.server.item.ItemPennant;
import com.pau101.fairylights.server.item.LightVariant;
import com.pau101.fairylights.server.item.crafting.Recipes;
import com.pau101.fairylights.server.jingle.JingleLibrary;
import com.pau101.fairylights.server.net.FLMessage;
import com.pau101.fairylights.server.net.clientbound.MessageJingle;
import com.pau101.fairylights.server.net.clientbound.MessageOpenEditLetteredConnectionGUI;
import com.pau101.fairylights.server.net.clientbound.MessageUpdateFastenerEntity;
import com.pau101.fairylights.server.net.serverbound.MessageConnectionInteraction;
import com.pau101.fairylights.server.net.serverbound.MessageEditLetteredConnection;
import com.pau101.fairylights.server.sound.FLSounds;
import com.pau101.fairylights.util.CalendarEvent;
import com.pau101.fairylights.util.crafting.GenericRecipe;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ServerProxy implements IMessageHandler<FLMessage, IMessage> {
	private int nextMessageId;

	private int nextEntityId;

	public void initConfig(FMLPreInitializationEvent event ) {
		Configurator.initConfig(event);
	}

	public void initSounds() {
		FLSounds.init();
	}

	public void initGUI() {
		FairyLights.fairyLightsTab = new CreativeTabsFairyLights();
	}

	public void initBlocks() {
		FairyLights.fastener = register(new BlockFastener(), "fastener", false);
	}

	public void initItems() {
		FairyLights.light = register(new ItemLight(), "light");
		FairyLights.hangingLights = register(new ItemConnectionHangingLights(), "hanging_lights");
		FairyLights.garland = register(new ItemConnectionGarland(), "garland");
		FairyLights.tinsel = register(new ItemConnectionTinsel(), "tinsel");
		FairyLights.pennantBunting = register(new ItemConnectionPennantBunting(), "pennant_bunting");
		FairyLights.letterBunting = register(new ItemConnectionLetterBunting(), "letter_bunting");
		FairyLights.pennant = register(new ItemPennant(), "pennant");
		FairyLights.ladder = register(new ItemLadder(), "ladder");
	}

	public void initCrafting() {
		GameRegistry.addRecipe(Recipes.FAIRY_LIGHTS);
		GameRegistry.addRecipe(Recipes.FAIRY_LIGHTS_AUGMENTATION);
		for (LightVariant variant : LightVariant.values()) {
			GameRegistry.addRecipe(variant.getRecipe());
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(FairyLights.garland, 2), "I-I", 'I', "ingotIron", '-', Blocks.VINE));
		GameRegistry.addRecipe(Recipes.TINSEL_GARLAND);
		GameRegistry.addRecipe(Recipes.PENNANT_BUNTING);
		GameRegistry.addRecipe(Recipes.PENNANT_BUNTING_AUGMENTATION);
		GameRegistry.addRecipe(Recipes.PENNANT);
		GameRegistry.addRecipe(Recipes.LETTER_BUNTING);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(FairyLights.ladder), "#/", "#/", "#/", '#', Blocks.LADDER, '/', "stickWood"));
		RecipeSorter.register(FairyLights.ID + ":generic", GenericRecipe.class, Category.SHAPED, "after:minecraft:shaped");
	}

	/*
	 * |\   /|    __     __     __     __
	 *  \|_|/    /  \   /  \   /  \   /  \
	 *  /. .\   |%%%%| |@@@@| |####| |$$$$|
	 * =\_Y_/=   \__/   \__/   \__/   \__/
	 */
	public void initEggs() {
		FairyLights.christmas = new CalendarEvent(Month.DECEMBER, 24, 26);
		FairyLights.christmasJingles = JingleLibrary.create("christmas");
		FairyLights.randomJingles = JingleLibrary.create("random");
		loadJingleLibraries();
	}

	protected void loadJingleLibraries() {
		JingleLibrary.loadAll();
	}

	public void initEntities() {
		GameRegistry.registerTileEntity(BlockEntityFastener.class, FairyLights.ID + ":fastener");
		registerEntity(EntityFenceFastener.class, "fastener", 160, Integer.MAX_VALUE, false);
		registerEntity(EntityLadder.class, "ladder", 160, 3, true);
	}

	public void initHandlers() {
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        CapabilityHandler.register();
	}

	public void initNetwork() {
		FairyLights.network = NetworkRegistry.INSTANCE.newSimpleChannel(FairyLights.ID);
		registerMessage(MessageJingle.class, Side.CLIENT);
		registerMessage(MessageUpdateFastenerEntity.class, Side.CLIENT);
		registerMessage(MessageOpenEditLetteredConnectionGUI.class, Side.CLIENT);
		registerMessage(MessageConnectionInteraction.class, Side.SERVER);
		registerMessage(MessageEditLetteredConnection.class, Side.SERVER);
	}

	public void initRenders() {}

	public void initRendersLate() {}

	@Nullable
	@Override
	public IMessage onMessage(FLMessage message, MessageContext ctx) {
		IThreadListener thread = FMLCommonHandler.instance().getWorldThread(ctx.netHandler);
		thread.addScheduledTask(() -> message.process(ctx));
		return null;
	}

	public static void sendToPlayersWatchingChunk(FLMessage message, World world, BlockPos pos) {
		PlayerChunkMap map = ((WorldServer) world).getPlayerChunkMap();
		PlayerChunkMapEntry e = map.getEntry(pos.getX() >> 4, pos.getZ() >> 4);
		if (e != null) {	
			e.sendPacket(FairyLights.network.getPacketFrom(message));
		}
	}

	public static void sendToPlayersWatchingEntity(FLMessage message, World world, Entity entity) {
		for (EntityPlayer player : ((WorldServer) world).getEntityTracker().getTrackingPlayers(entity)) {
			FairyLights.network.sendTo(message, (EntityPlayerMP) player);
		}
		if (entity instanceof EntityPlayerMP) {
			FairyLights.network.sendTo(message, (EntityPlayerMP) entity);
		}
	}

	private void registerMessage(Class<? extends FLMessage> clazz, Side toSide) {
		FairyLights.network.registerMessage(this, clazz, nextMessageId++, toSide);
	}

	private <B extends Block> B register(B block, String name) {
		return register(block, name, true);
	}

	private <B extends Block> B register(Block block, String name, boolean hasItem) {
		GameRegistry.register(block.setRegistryName(name));
		if (hasItem) {
			GameRegistry.register(new ItemBlock(block).setRegistryName(name));
		}
		return (B) block.setUnlocalizedName(toUnlocalizedName(name));
	}

	private <T extends Item> T register(T item, String name) {
		item.setRegistryName(name);
		GameRegistry.register(item);
		return (T) item.setUnlocalizedName(toUnlocalizedName(name));
	}

	private void registerEntity(Class<? extends Entity> clazz, String id, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
		EntityRegistry.registerModEntity(new ResourceLocation(FairyLights.ID, id), clazz, id, nextEntityId++, FairyLights.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
	}

	private String toUnlocalizedName(String name) {
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
	}
}
