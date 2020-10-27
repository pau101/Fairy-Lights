function initializeCoreMod() {
Java.type('net.minecraftforge.coremod.api.ASMAPI').loadFile('easycorelib.js')

easycore.include('me')
easycore.include('it')

var ClientEventHandler = me.paulf.fairylights.client.ClientEventHandler,
    FairyLights = me.paulf.fairylights.FairyLights,
    ItemStack = net.minecraft.item.ItemStack,
    PlayerInventory = net.minecraft.entity.player.PlayerInventory,
    NonNullList = net.minecraft.util.NonNullList,
    GameRenderer = net.minecraft.client.renderer.GameRenderer,
    RayTraceResult = net.minecraft.util.math.RayTraceResult

easycore.inMethod(PlayerInventory.func_194014_c(ItemStack))
    .atFirst(invokespecial(PlayerInventory.func_184431_b(ItemStack, ItemStack), boolean)).append(
        aload(1),
        aload(0),
        getfield(PlayerInventory.field_70462_a, NonNullList),
        iload(2),
        invokevirtual(NonNullList.get(int), java.lang.Object),
        checkcast(ItemStack),
        invokestatic(FairyLights.ingredientMatches(boolean, ItemStack, ItemStack), boolean)
    )

easycore.inMethod(GameRenderer.func_78473_a(float))
    .atEach(_return).prepend(
        invokestatic(ClientEventHandler.updateHitConnection())
    )

return easycore.build()
}