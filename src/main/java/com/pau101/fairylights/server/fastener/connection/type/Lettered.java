package com.pau101.fairylights.server.fastener.connection.type;

import java.util.function.Function;

import com.pau101.fairylights.server.fastener.connection.PlayerAction;
import com.pau101.fairylights.server.fastener.connection.collision.Intersection;
import com.pau101.fairylights.util.styledstring.StyledString;
import com.pau101.fairylights.util.styledstring.StylingPresence;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

public interface Lettered {
	int MAX_TEXT_LENGTH = 64;
	
	Function<Character, Character> IDENTITY_CHARACTER_TRANSFORMER = c -> c;

	default StylingPresence getSupportedStyling() {
		return StylingPresence.ALL;
	}

	default boolean isSupportedCharacter(char chr) {
		return Character.isValidCodePoint(chr) && !Character.isISOControl(chr);
	}

	default boolean isSuppportedText(StyledString text) {
		for (int i = 0; i < text.length(); i++) {
			if (!isSupportedCharacter(text.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	default int toSupportedCharacter(int chr) {
		return chr;
	}

	void setText(StyledString text);

	StyledString getText();

	default Function<Character, Character> getCharInputTransformer() {
		return IDENTITY_CHARACTER_TRANSFORMER;
	}

	GuiScreen createTextGUI();

	default boolean openTextGui(EntityPlayer player, PlayerAction action, Intersection intersection) {
		if (action == PlayerAction.INTERACT && player.isSneaking()) {
			Minecraft.getMinecraft().displayGuiScreen(createTextGUI());
			return false;
		}
		return true;
	}
}
