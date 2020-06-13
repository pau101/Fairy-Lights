package me.paulf.fairylights.server.fastener.connection;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.fastener.connection.type.garland.GarlandTinselConnection;
import me.paulf.fairylights.server.fastener.connection.type.garland.GarlandVineConnection;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.HangingLightsConnection;
import me.paulf.fairylights.server.fastener.connection.type.letter.LetterBuntingConnection;
import me.paulf.fairylights.server.fastener.connection.type.pennant.PennantBuntingConnection;
import me.paulf.fairylights.server.item.FLItems;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public final class ConnectionTypes {
    private ConnectionTypes() {}

    public static final DeferredRegister<ConnectionType<?>> REG = new DeferredRegister<>(FairyLights.CONNECTION_TYPES, FairyLights.ID);

    public static final RegistryObject<ConnectionType<HangingLightsConnection>> HANGING_LIGHTS = REG.register("hanging_lights",
        () -> ConnectionType.Builder.create(HangingLightsConnection::new).item(FLItems.HANGING_LIGHTS).build()
    );

    public static final RegistryObject<ConnectionType<GarlandVineConnection>> VINE_GARLAND = REG.register("vine_garland",
        () -> ConnectionType.Builder.create(GarlandVineConnection::new).item(FLItems.GARLAND).build()
    );

    public static final RegistryObject<ConnectionType<GarlandTinselConnection>> TINSEL_GARLAND = REG.register("tinsel_garland",
        () -> ConnectionType.Builder.create(GarlandTinselConnection::new).item(FLItems.TINSEL).build()
    );

    public static final RegistryObject<ConnectionType<PennantBuntingConnection>> PENNANT_BUNTING = REG.register("pennant_bunting",
        () -> ConnectionType.Builder.create(PennantBuntingConnection::new).item(FLItems.PENNANT_BUNTING).build()
    );

    public static final RegistryObject<ConnectionType<LetterBuntingConnection>> LETTER_BUNTING = REG.register("letter_bunting",
        () -> ConnectionType.Builder.create(LetterBuntingConnection::new).item(FLItems.LETTER_BUNTING).build()
    );
}
