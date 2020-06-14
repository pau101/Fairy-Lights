package me.paulf.fairylights.server.fastener.connection.type.letter;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.gui.EditLetteredConnectionScreen;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.connection.PlayerAction;
import me.paulf.fairylights.server.fastener.connection.collision.Intersection;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.server.fastener.connection.type.Lettered;
import me.paulf.fairylights.server.net.clientbound.OpenEditLetteredConnectionScreenMessage;
import me.paulf.fairylights.util.styledstring.StyledString;
import me.paulf.fairylights.util.styledstring.StylingPresence;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

public final class LetterBuntingConnection extends Connection implements Lettered {
    public static final SymbolSet SYMBOLS = new SymbolSet.Builder(7, "0-9, A-Z, &, !, ?")
        .add(" 0123456789ABCDEFGHJKLMNOPQRSTUVWXYZ&?", 6)
        .add("I", 4)
        .add("!", 2)
        .build();

    private static final float TRACKING = 1.0F / 16.0F;

    private static final StylingPresence SUPPORTED_STYLING = new StylingPresence(true, false, false, false, false, false);

    private StyledString text;

    private Letter[] letters = new Letter[0];

    public LetterBuntingConnection(final ConnectionType<? extends LetterBuntingConnection> type, final World world, final Fastener<?> fastener, final UUID uuid) {
        super(type, world, fastener, uuid);
        this.text = new StyledString();
    }

    @Override
    public float getRadius() {
        return 0.9F / 32;
    }

    public Letter[] getLetters() {
        return this.letters;
    }

    @Override
    public void processClientAction(final PlayerEntity player, final PlayerAction action, final Intersection intersection) {
        if (this.openTextGui(player, action, intersection)) {
            super.processClientAction(player, action, intersection);
        }
    }

    @Override
    public void onConnect(final World world, final PlayerEntity user, final ItemStack heldStack) {
        if (this.text.isEmpty()) {
            FairyLights.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) user), new OpenEditLetteredConnectionScreenMessage<>(this));
        }
    }

    @Override
    protected void onUpdate() {
        for (final Letter letter : this.letters) {
            letter.tick(this.world);
        }
    }

    @Override
    protected void onCalculateCatenary(final boolean relocated) {
        this.updateLetters();
    }

    private void updateLetters() {
        if (this.text.isEmpty()) {
            this.letters = new Letter[0];
        } else {
            final Catenary catenary = this.getCatenary();
            float textWidth = 0;
            int textLen = 0;
            final float[] pointOffsets = new float[this.text.length()];
            final float catLength = catenary.getLength();
            for (int i = 0; i < this.text.length(); i++) {
                final float w = SYMBOLS.getWidth(this.text.charAt(i));
                pointOffsets[i] = textWidth + w / 2.0F;
                textWidth += w + TRACKING;
                if (textWidth > catLength) {
                    break;
                }
                textLen++;
            }
            final float offset = catLength / 2 - textWidth / 2;
            for (int i = 0; i < textLen; i++) {
                pointOffsets[i] += offset;
            }
            int pointIdx = 0;
            final Letter[] prevLetters = this.letters;
            final List<Letter> letters = new ArrayList<>(this.text.length());
            final Catenary.SegmentIterator it = catenary.iterator();
            float distance = 0;
            while (it.next()) {
                final float length = it.getLength();
                for (int n = pointIdx; n < textLen; n++) {
                    final float pointOffset = pointOffsets[n];
                    if (pointOffset < distance + length) {
                        final float t = (pointOffset - distance) / length;
                        final Vec3d point = new Vec3d(it.getX(t), it.getY(t), it.getZ(t));
                        final Letter letter;
                        if (prevLetters != null && pointIdx < prevLetters.length) {
                            letter = prevLetters[pointIdx];
                            letter.set(point, it.getYaw(), it.getPitch());
                            letter.set(this.text.charAt(pointIdx), this.text.styleAt(pointIdx));
                        } else {
                            letter = new Letter(pointIdx, point, it.getYaw(), it.getPitch(), SYMBOLS, this.text.charAt(pointIdx), this.text.styleAt(pointIdx));
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
            this.letters = letters.toArray(new Letter[0]);
        }
    }

    @Override
    public StylingPresence getSupportedStyling() {
        return SUPPORTED_STYLING;
    }

    @Override
    public boolean isSupportedCharacter(final int chr) {
        return SYMBOLS.contains(chr);
    }

    @Override
    public boolean isSupportedText(final StyledString text) {
        float len = 0;
        final float available = this.getCatenary().getLength();
        for (int i = 0; i < text.length(); i++) {
            final float w = SYMBOLS.getWidth(text.charAt(i));
            len += w + TRACKING;
            if (len > available) {
                return false;
            }
            if (!text.styleAt(i).isPlain()) {
                return false;
            }
        }
        return Lettered.super.isSupportedText(text);
    }

    @Override
    public void setText(final StyledString text) {
        this.text = text;
        this.computeCatenary();
    }

    @Override
    public StyledString getText() {
        return this.text;
    }

    @Override
    public Function<String, String> getInputTransformer() {
        return str -> Normalizer.normalize(str, Normalizer.Form.NFKD).replaceAll("[\\p{Mn}\\p{Sk}]", "").toUpperCase(Locale.ROOT);
    }

    @Override
    public String getAllowedDescription() {
        return SYMBOLS.getDescription();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Screen createTextGUI() {
        return new EditLetteredConnectionScreen<>(this);
    }

    @Override
    public CompoundNBT serializeLogic() {
        final CompoundNBT compound = super.serializeLogic();
        compound.put("text", StyledString.serialize(this.text));
        return compound;
    }

    @Override
    public void deserializeLogic(final CompoundNBT compound) {
        super.deserializeLogic(compound);
        this.text = StyledString.deserialize(compound.getCompound("text"));
    }
}
