package com.pau101.fairylights.server.fastener.connection.type.letter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.client.gui.GuiEditLetteredConnection;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.connection.Catenary;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.fastener.connection.PlayerAction;
import com.pau101.fairylights.server.fastener.connection.Segment;
import com.pau101.fairylights.server.fastener.connection.collision.Intersection;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.fastener.connection.type.Lettered;
import com.pau101.fairylights.server.net.clientbound.MessageOpenEditLetteredConnectionGUI;
import com.pau101.fairylights.util.styledstring.StyledString;
import com.pau101.fairylights.util.styledstring.StylingPresence;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ConnectionLetterBunting extends Connection implements Lettered {
	public static final SymbolSet SYMBOLS = SymbolSet.from(6, 10, "0,6,1,3,2,6,3,6,4,6,5,6,6,6,7,6,8,6,9,6,A,8,B,7,C,8,D,7,E,7,F,7,G,8,H,7,I,2,J,6,K,8,L,7,M,10,N,8,O,8,P,7,Q,8,R,7,S,7,T,8,U,7,V,8,W,10,X,8,Y,8,Z,7, ,6");

	private static final float TRACKING = 2;

	private static final StylingPresence SUPPORTED_STYLING = new StylingPresence(true, false, false, false, false, false);

	private StyledString text;

	private Letter[] letters = new Letter[0];

	@Nullable
	private Letter[] prevLetters;

	public ConnectionLetterBunting(World world, Fastener<?> fastener, UUID uuid, Fastener<?> destination, boolean isOrigin, NBTTagCompound compound) {
		super(world, fastener, uuid, destination, isOrigin, compound);
	}

	public ConnectionLetterBunting(World world, Fastener<?> fastener, UUID uuid) {
		super(world, fastener, uuid);
		text = new StyledString();
	}

	@Override
	public float getRadius() {
		return 0.9F / 32;
	}

	public Letter[] getLetters() {
		return letters;
	}

	public Letter[] getPrevLetters() {
		return Objects.firstNonNull(prevLetters, letters);
	}

	@Override
	public ConnectionType getType() {
		return ConnectionType.LETTER_BUNTING;
	}

	@Override
	public Catenary createCatenary(Vec3d to) {
		return Catenary.from(to, false);
	}

	@Override
	public void processClientAction(EntityPlayer player, PlayerAction action, Intersection intersection) {
		if (openTextGui(player, action, intersection)) {
			super.processClientAction(player, action, intersection);
		}
	}

	@Override
	public void onConnect(World world, EntityPlayer user, ItemStack heldStack) {
		if (text.isEmpty()) {
			FairyLights.network.sendTo(new MessageOpenEditLetteredConnectionGUI(this), (EntityPlayerMP) user);
		}
	}

	@Override
	protected void onUpdateEarly() {
		prevLetters = letters;
		for (Letter letter : letters) {
			letter.tick();
		}
	}

	@Override
	protected void onCalculateCatenary() {
		updateLetters();
	}

	private void updateLetters() {
		if (text.isEmpty()) {
			prevLetters = letters;
			letters = new Letter[0];
		} else {
			Catenary catenary = getCatenary();
			float textWidth = 0;
			int textLen = 0;
			float[] pointOffsets = new float[text.length()];
			float catLength = catenary.getLength();
			for (int i = 0; i < text.length(); i++) {
				float w = SYMBOLS.getWidth(text.charAt(i));
				pointOffsets[i] = textWidth + w / 2;
				textWidth += w + TRACKING;
				if (textWidth > catLength) {
					break;
				}
				textLen++;
			}
			float offset = catLength / 2 - textWidth / 2;
			for (int i = 0; i < textLen; i++) {
				pointOffsets[i] += offset;
			}
			int pointIdx = 0;
			prevLetters = letters;
			boolean hasPrevLetters = prevLetters != null;
			List<Letter> letters = new ArrayList<>(text.length());
			Segment[] segments = catenary.getSegments();
			double distance = 0;
			for (int i = 0; i < segments.length; i++) {
				Segment seg = segments[i];
				double length = seg.getLength();
				for (int n = pointIdx; n < textLen; n++) {
					float pointOffset = pointOffsets[n];
					if (pointOffset < distance + length) {
						double t = (pointOffset - distance) / length;
						Vec3d point = seg.pointAt(t);
						Letter letter = new Letter(pointIdx, point, seg.getRotation(), SYMBOLS, text.charAt(pointIdx));
						if (hasPrevLetters && pointIdx < prevLetters.length) {
							letter.inherit(prevLetters[pointIdx]);
						}
						letters.add(letter);
						pointIdx++;
					} else {
						break;
					}
				}
				if (pointIdx == textLen) {
					break;
				}
				distance += length;
			}
			this.letters = letters.toArray(new Letter[letters.size()]);
		}
	}

	@Override
	public StylingPresence getSupportedStyling() {
		return SUPPORTED_STYLING;
	}

	@Override
	public boolean isSupportedCharacter(char chr) {
		return SYMBOLS.contains(chr);
	}

	@Override
	public boolean isSuppportedText(StyledString text) {
		float len = 0;
		float available = getCatenary().getLength();
		for (int i = 0; i < text.length(); i++) {
			float w = SYMBOLS.getWidth(text.charAt(i));
			len += w + TRACKING;
			if (len > available) {
				return false;
			}
			if (!text.styleAt(i).isPlain()) {
				return false;
			}
		}
		return Lettered.super.isSuppportedText(text);
	}

	@Override
	public void setText(StyledString text) {
		this.text = text;
		dataUpdateState = true;
	}

	@Override
	public StyledString getText() {
		return text;
	}

	@Override
	public Function<Character, Character> getCharInputTransformer() {
		return Character::toUpperCase;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen createTextGUI() {
		return new GuiEditLetteredConnection<>(this);
	}

	@Override
	public NBTTagCompound serializeLogic() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("text", StyledString.serialize(text));
		return compound;
	}

	@Override
	public void deserializeLogic(NBTTagCompound compound) {
		text = StyledString.deserialize(compound.getCompoundTag("text"));
	}
}
