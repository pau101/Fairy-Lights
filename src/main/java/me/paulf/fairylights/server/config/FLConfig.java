package me.paulf.fairylights.server.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class FLConfig {
	private FLConfig() {}

	private static class General {
		private final ForgeConfigSpec.ConfigValue<Boolean> jingleEnabled;

		private final ForgeConfigSpec.ConfigValue<Integer> jingleAmplitude;

		private General(final ForgeConfigSpec.Builder builder) {
			builder.push("general");
			// TODO: lang
			this.jingleEnabled = builder.comment("If true jingles will play during Christmas.")
				.translation("config.fairylights.christmas_jingles")
				.define("christmas_jingles", true);
			this.jingleAmplitude = builder.comment("The distance that jingles can be heard in blocks.")
				.translation("config.fairylights.jingle_amplitude")
				.defineInRange("jingles_amplitude", 40, 1, Integer.MAX_VALUE);
			builder.pop();
		}
	}

	private static final General GENERAL;

	public static final ForgeConfigSpec GENERAL_SPEC;

	static {
		final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		GENERAL = new General(builder);
		GENERAL_SPEC = builder.build();
	}

	public static boolean isJingleEnabled() {
		return GENERAL.jingleEnabled.get();
	}

	public static int getJingleAmplitude() {
		return GENERAL.jingleAmplitude.get();
	}
}
