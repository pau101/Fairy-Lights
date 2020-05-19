package me.paulf.fairylights.server.fastener.connection.type;

import me.paulf.fairylights.server.fastener.connection.PlayerAction;
import me.paulf.fairylights.server.fastener.connection.collision.Intersection;
import me.paulf.fairylights.util.styledstring.StyledString;
import me.paulf.fairylights.util.styledstring.StylingPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Function;

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
