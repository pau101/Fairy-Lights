package me.paulf.fairylights.client;

import net.minecraftforge.common.ForgeConfigSpec;

public final class FLClientConfig {
    private FLClientConfig() {}

    public static final class Tutorial {
        public final ForgeConfigSpec.ConfigValue<String> progress;

        private Tutorial(final ForgeConfigSpec.Builder builder) {
            builder.push("tutorial");
            this.progress = builder
                .comment(
                    "The hanging lights tutorial progress, once any light item enters the inventory a",
                    " toast appears prompting to craft hanging lights. A finished tutorial progress",
                    " value is 'complete' and an unstarted tutorial is 'none'."
                )
                .define("progress", "none");
            builder.pop();
        }
    }

    public static final Tutorial TUTORIAL;

    public static final ForgeConfigSpec SPEC;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        TUTORIAL = new Tutorial(builder);
        SPEC = builder.build();
    }
}
