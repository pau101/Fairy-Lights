package com.pau101.fairylights.server.net.clientbound;

import com.pau101.fairylights.client.gui.GuiEditLetteredConnection;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.fastener.connection.type.Lettered;
import com.pau101.fairylights.server.net.MessageConnection;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageOpenEditLetteredConnectionGUI<C extends Connection & Lettered> extends MessageConnection<C> {
	public MessageOpenEditLetteredConnectionGUI() {}

	public MessageOpenEditLetteredConnectionGUI(C connection) {
		super(connection);
	}

	@Override
	protected boolean isInstanceOfType(Class<? extends Connection> connection) {
		return Lettered.class.isAssignableFrom(connection);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected World getWorld(MessageContext ctx) {
		return Minecraft.getMinecraft().world;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void process(MessageContext ctx, C connection) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiEditLetteredConnection<C>(connection));
	}
}
