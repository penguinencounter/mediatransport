package penguinencounter.mediatransport.mixin;

import at.petrak.hexcasting.interop.HexInterop;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.api.PatchouliAPI;

@Mixin(value = HexInterop.class, remap = false)
public abstract class HexForceInteropVisibleMixin {
    @Final
    @Shadow
    public static String PATCHOULI_ANY_INTEROP_FLAG;

    @Inject(method = "initPatchouli", at = @At("RETURN"))
    private static void mediatransport$inject(CallbackInfo ci) {
        // the entire point of this addon is interop so we just force it true all the time
        PatchouliAPI.get().setConfigFlag(PATCHOULI_ANY_INTEROP_FLAG, true);
    }
}
