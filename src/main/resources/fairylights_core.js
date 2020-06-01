function initializeCoreMod() {
Java.type('net.minecraftforge.coremod.api.ASMAPI').loadFile('easycorelib.js')

easycore.include('me')

var ClientEventHandler = me.paulf.fairylights.client.ClientEventHandler,
    FairyLights = me.paulf.fairylights.FairyLights,
    WorldRenderer = net.minecraft.client.renderer.WorldRenderer,
    RayTraceResult = net.minecraft.util.math.RayTraceResult,
    MatrixStack = com.mojang.blaze3d.matrix.MatrixStack,
    ActiveRenderInfo = net.minecraft.client.renderer.ActiveRenderInfo,
    ItemStack = net.minecraft.item.ItemStack,
    PlayerInventory = net.minecraft.entity.player.PlayerInventory,
    NonNullList = net.minecraft.util.NonNullList

easycore.inMethod(WorldRenderer.func_228426_a_(
        MatrixStack,
        float,
        long,
        boolean,
        ActiveRenderInfo,
        net.minecraft.client.renderer.GameRenderer,
        net.minecraft.client.renderer.LightTexture,
        net.minecraft.client.renderer.Matrix4f)
    )
    .atLast(invokevirtual(RayTraceResult.func_216346_c())).prepend(
        aload(0),
        aload(6),
        fload(2),
        aload(1),
        aload(38),
        invokestatic(ClientEventHandler.drawSelectionBox(RayTraceResult, WorldRenderer, ActiveRenderInfo, float, MatrixStack, net.minecraft.client.renderer.IRenderTypeBuffer), RayTraceResult)
    )

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

return easycore.build()
}