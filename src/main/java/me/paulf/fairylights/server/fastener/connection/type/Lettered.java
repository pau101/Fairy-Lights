package me.paulf.fairylights.server.fastener.connection.type;

import me.paulf.fairylights.server.fastener.connection.*;
import me.paulf.fairylights.server.fastener.connection.collision.*;
import me.paulf.fairylights.util.styledstring.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.entity.player.*;

import java.util.function.*;

public interface Lettered {
    default StylingPresence getSupportedStyling() {
        return StylingPresence.ALL;
    }

    default boolean isSupportedCharacter(final int chr) {
        return Character.isValidCodePoint(chr) && !Character.isISOControl(chr);
    }

    default boolean isSuppportedText(final StyledString text) {
        for (int i = 0; i < text.length(); i++) {
            if (!this.isSupportedCharacter(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    void setText(StyledString text);

    StyledString getText();

    default String getAllowedDescription() {
        return "";
    }

    default Function<String, String> getInputTransformer() {
        return Function.identity();
    }

    Screen createTextGUI();

    default boolean openTextGui(final PlayerEntity player, final PlayerAction action, final Intersection intersection) {
        if (action == PlayerAction.INTERACT && player.isSneaking()) {
            Minecraft.getInstance().displayGuiScreen(this.createTextGUI());
            return false;
        }
        return true;
    }
}
