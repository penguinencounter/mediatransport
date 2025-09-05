package penguinencounter.mediatransport.mixin;

import org.figuramc.figura.server.FiguraServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import penguinencounter.mediatransport.MediaTransportServer;

@Mixin(FiguraServer.class)
public abstract class FiguraServerCreatedHookMixin {
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void hookInit(CallbackInfo ci) {
        MediaTransportServer.INSTANCE.initializeFigura();
    }
}
