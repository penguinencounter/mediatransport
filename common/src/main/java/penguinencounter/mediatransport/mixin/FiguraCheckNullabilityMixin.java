package penguinencounter.mediatransport.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import org.figuramc.figura.lua.FiguraLuaRuntime;
import org.figuramc.figura.lua.api.ServerPacketsAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPacketsAPI.class, remap = false)
public abstract class FiguraCheckNullabilityMixin {
    @Inject(
            method = "handlePacket",
            at = @At(value = "FIELD", target = "Lorg/figuramc/figura/lua/FiguraLuaRuntime;serverPackets:Lorg/figuramc/figura/lua/api/ServerPacketsAPI;"),
            cancellable = true
    )
    private static void mediatransport$handleCrashedAvatarCleanly(
            int id, byte[] data, CallbackInfo ci, @Local FiguraLuaRuntime runtime) {
        if (runtime == null) ci.cancel(); // instead of throwing NPE
    }
}
