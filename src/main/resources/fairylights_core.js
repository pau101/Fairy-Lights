function initializeCoreMod() {
Java.type('net.minecraftforge.coremod.api.ASMAPI').loadFile('easycorelib.js')

easycore.include('me')

var Mod = me.paulf.fairylights.client.ClientEventHandler,
    WorldRenderer = net.minecraft.client.renderer.WorldRenderer,
    RayTraceResult = net.minecraft.util.math.RayTraceResult,
    MatrixStack = com.mojang.blaze3d.matrix.MatrixStack,
    ActiveRenderInfo = net.minecraft.client.renderer.ActiveRenderInfo

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
        invokestatic(Mod.drawSelectionBox(RayTraceResult, WorldRenderer, ActiveRenderInfo, float, MatrixStack, net.minecraft.client.renderer.IRenderTypeBuffer), RayTraceResult)
    )

return easycore.build()
}