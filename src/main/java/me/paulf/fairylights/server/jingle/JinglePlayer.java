package me.paulf.fairylights.server.jingle;

import com.google.common.collect.Sets;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.server.sound.FLSounds;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JinglePlayer {
    private static final Set<String> WITH_LOVE = Sets.newHashSet("my_anthem", "im_fine_thank_you");

    private State<?> state = new NotPlayingState();

    @Nullable
    public Jingle getJingle() {
        return this.state.getJingle();
    }

    public boolean isPlaying() {
        return this.state.isPlaying();
    }

    public float getProgress() {
        return this.state.getProgress();
    }

    public void play(final JingleLibrary library, final Jingle jingle, final int lightOffset) {
        this.state = new PlayingState(library, jingle, lightOffset);
    }

    public void tick(final World world, final Vec3d origin, final Light[] lights, final boolean isClient) {
        this.state = this.state.tick(world, origin, lights, isClient);
    }

    public CompoundNBT serialize() {
        return StateType.serialize(this.state);
    }

    public void deserialize(final CompoundNBT compound) {
        this.state = StateType.deserialize(compound);
    }

    private enum StateType {
        NOT_PLAYING(NotPlayingState.FACTORY),
        PLAYING(PlayingState.FACTORY);

        private static final Map<String, StateType> MAP = Stream.of(values())
            .collect(Collectors.toMap(StateType::getId, Function.identity()));

        private final StateFactory<?> factory;

        StateType(final StateFactory<?> factory) {
            this.factory = factory;
        }

        private String getId() {
            return this.factory.getId();
        }

        private StateFactory<?> getFactory() {
            return this.factory;
        }

        public static <S extends State<S>> CompoundNBT serialize(final State<S> state) {
            final StateFactory<S> factory = state.getFactory();
            final CompoundNBT compound = new CompoundNBT();
            compound.putString("state", factory.getId());
            compound.put("data", factory.serialize(state.resolve()));
            return compound;
        }

        public static State<?> deserialize(final CompoundNBT compound) {
            return MAP.getOrDefault(compound.getString("state"), NOT_PLAYING)
                .getFactory()
                .deserialize(compound.getCompound("data"));
        }
    }

    private abstract static class StateFactory<S extends State<S>> {
        public abstract String getId();

        public abstract CompoundNBT serialize(S state);

        public abstract State<?> deserialize(CompoundNBT compound);
    }

    private abstract static class State<S extends State<S>> {
        public abstract Jingle getJingle();

        public abstract boolean isPlaying();

        public abstract float getProgress();

        public abstract State<?> tick(World world, Vec3d origin, Light[] lights, boolean isClient);

        public abstract StateFactory<S> getFactory();

        public abstract S resolve();
    }

    private static final class NotPlayingState extends State<NotPlayingState> {
        public static final StateFactory<NotPlayingState> FACTORY = newFactory();

        @Override
        public Jingle getJingle() {
            return null;
        }

        @Override
        public boolean isPlaying() {
            return false;
        }

        @Override
        public float getProgress() {
            return 0;
        }

        @Override
        public State<?> tick(final World world, final Vec3d origin, final Light[] lights, final boolean isClient) {
            return this;
        }

        @Override
        public StateFactory<NotPlayingState> getFactory() {
            return FACTORY;
        }

        @Override
        public NotPlayingState resolve() {
            return this;
        }

        private static StateFactory<NotPlayingState> newFactory() {
            return new StateFactory<NotPlayingState>() {
                @Override
                public String getId() {
                    return "not_playing";
                }

                @Override
                public CompoundNBT serialize(final NotPlayingState state) {
                    return new CompoundNBT();
                }

                @Override
                public NotPlayingState deserialize(final CompoundNBT compound) {
                    return new NotPlayingState();
                }
            };
        }
    }

    private static final class PlayingState extends State<PlayingState> {
        public static final StateFactory<PlayingState> FACTORY = newFactory();

        private final JingleLibrary library;

        private final Jingle jingle;

        private final int lightOffset;

        private final List<Jingle.PlayTick> playTicks;

        private final int length;

        private final BasicParticleType[] noteParticle;

        private int index;

        private int rest;

        private int time;

        private PlayingState(final JingleLibrary library, final Jingle jingle, final int lightOffset) {
            this(library, jingle, lightOffset, jingle.getPlayTicks(), jingle.getLength(), getParticles(jingle));
        }

        private PlayingState(final JingleLibrary library, final Jingle jingle, final int lightOffset, final List<Jingle.PlayTick> playTicks, final int length, final BasicParticleType[] noteParticle) {
            this.library = library;
            this.jingle = jingle;
            this.lightOffset = lightOffset;
            this.playTicks = playTicks;
            this.length = length;
            this.noteParticle = noteParticle;
        }

        @Override
        public Jingle getJingle() {
            return this.jingle;
        }

        @Override
        public boolean isPlaying() {
            return true;
        }

        @Override
        public float getProgress() {
            return this.time / (float) this.length;
        }

        @Override
        public State<?> tick(final World world, final Vec3d origin, final Light[] lights, final boolean isClient) {
            this.time++;
            if (this.rest <= 0) {
                if (this.index >= this.playTicks.size()) {
                    return new NotPlayingState();
                }
                final Jingle.PlayTick playTick = this.playTicks.get(this.index++);
                this.rest = playTick.getLength() - 1;
                if (isClient) {
                    this.play(world, origin, lights, playTick);
                }
            } else {
                this.rest--;
            }
            return this;
        }

        private void play(final World world, final Vec3d origin, final Light[] lights, final Jingle.PlayTick playTick) {
            for (final int note : playTick.getNotes()) {
                final int idx = note - this.jingle.getLowestNote() + this.lightOffset;
                if (idx >= 0 && idx < lights.length) {
                    lights[idx].jingle(world, origin, note, FLSounds.JINGLE_BELL.get(), this.noteParticle);
                }
            }
        }

        @Override
        public StateFactory<PlayingState> getFactory() {
            return FACTORY;
        }

        @Override
        public PlayingState resolve() {
            return this;
        }

        private static StateFactory<PlayingState> newFactory() {
            return new StateFactory<PlayingState>() {
                @Override
                public String getId() {
                    return "playing";
                }

                @Override
                public CompoundNBT serialize(final PlayingState state) {
                    final CompoundNBT compound = new CompoundNBT();
                    compound.putString("library", state.library.getName().toString());
                    compound.putString("jingle", state.jingle.getId());
                    compound.putInt("lightOffset", state.lightOffset);
                    compound.putInt("index", state.index);
                    compound.putInt("rest", state.rest);
                    compound.putInt("time", state.time);
                    return compound;
                }

                @Override
                public State<?> deserialize(final CompoundNBT compound) {
                    final JingleLibrary library = JingleLibrary.fromName(ResourceLocation.tryCreate(compound.getString("library")));
                    final Jingle jingle = library.get(compound.getString("jingle"));
                    if (jingle == null) {
                        return new NotPlayingState();
                    }
                    final int lightOffset = compound.getInt("lightOffset");
                    final PlayingState state = new PlayingState(library, jingle, lightOffset);
                    state.index = compound.getInt("index");
                    state.rest = compound.getInt("rest");
                    state.time = compound.getInt("time");
                    return state;
                }
            };
        }

        private static BasicParticleType[] getParticles(final Jingle jingle) {
            if ("playing_with_fire".equals(jingle.getId())) {
                return new BasicParticleType[]{ParticleTypes.NOTE, ParticleTypes.LAVA};
            }
            if (WITH_LOVE.contains(jingle.getId())) {
                return new BasicParticleType[]{ParticleTypes.NOTE, ParticleTypes.HEART};
            }
            return new BasicParticleType[]{ParticleTypes.NOTE};
        }
    }
}
