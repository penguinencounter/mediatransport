package penguinencounter.mediatransport.patchi

import com.google.gson.annotations.SerializedName
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import vazkii.patchouli.api.IComponentRenderContext
import vazkii.patchouli.api.ICustomComponent
import vazkii.patchouli.api.IVariable
import java.util.function.UnaryOperator

class ByteDiagramComponent : ICustomComponent {
    @Transient private var x: Int = 0
    @Transient private var y: Int = 0

    public

    override fun build(componentX: Int, componentY: Int, pageNum: Int) {
        x = componentX
        y = componentY
    }

    override fun render(
        graphics: GuiGraphics,
        context: IComponentRenderContext,
        pticks: Float,
        mouseX: Int,
        mouseY: Int
    ) {
        val theText = Component.literal("it renders!").withStyle(context.font)
        graphics.drawString(Minecraft.getInstance().font, theText, x, y, -1, true)
    }

    override fun onVariablesAvailable(lookup: UnaryOperator<IVariable?>?) {
//        TODO("Not yet implemented")
    }
}