package penguinencounter.mediatransport.patchi

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import vazkii.patchouli.api.IComponentRenderContext
import vazkii.patchouli.api.ICustomComponent
import vazkii.patchouli.api.IVariable
import java.util.function.UnaryOperator

class AlternatePageComponent : ICustomComponent {
    companion object {
        private const val LEFT_X_OFFSET = -7
        private const val RIGHT_X_OFFSET = -6
        private const val Y_OFFSET = -11
        private val LEFT = ResourceLocation("mediatransport", "textures/gui/altn_page_left.png")
        private val RIGHT = ResourceLocation("mediatransport", "textures/gui/altn_page_right.png")
        private const val U = 0f
        private const val V = 0f
        private const val W = 129
        private const val H = 165
        private const val TEX_DIM = 256
    }

    @Transient private var x: Int = 0
    @Transient private var y: Int = Y_OFFSET
    @Transient private lateinit var texture: ResourceLocation

    override fun build(componentX: Int, componentY: Int, pageNum: Int) {
        val isLeft = pageNum % 2 == 0
        x = if (isLeft) LEFT_X_OFFSET else RIGHT_X_OFFSET
        texture = if (isLeft) LEFT else RIGHT
    }

    override fun render(
        graphics: GuiGraphics,
        context: IComponentRenderContext,
        pticks: Float,
        mouseX: Int,
        mouseY: Int
    ) {
        graphics.pose().pushPose()
        graphics.pose().translate(x.toFloat(), y.toFloat(), 0f)
        graphics.setColor(1f, 1f, 1f, 1f)
        RenderSystem.enableBlend()
        graphics.blit(texture, 0, 0, U, V, W, H, TEX_DIM, TEX_DIM)
        graphics.pose().popPose()
    }

    override fun onVariablesAvailable(lookup: UnaryOperator<IVariable?>?) {}
}