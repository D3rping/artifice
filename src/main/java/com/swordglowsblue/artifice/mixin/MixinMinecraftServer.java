package com.swordglowsblue.artifice.mixin;

import com.swordglowsblue.artifice.api.Artifice;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import org.apache.logging.log4j.LogManager;

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {
    @Shadow private ResourcePackManager<ResourcePackProfile> dataPackManager;
    
    @Shadow public abstract void reload();
    
    @SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At("RETURN"))
    private void registerPackCreator(CallbackInfo cbi) {
        LogManager.getLogger().info("TEST");
        this.dataPackManager.registerProvider(new ResourcePackProvider() {
            public <T extends ResourcePackProfile> void register(Map<String, T> packs, ResourcePackProfile.Factory<T> factory) {
                for(Identifier id : Artifice.DATA.getIds()) {
                    LogManager.getLogger().info("data "+id);
                    packs.put(id.toString(), (T) Artifice.DATA.get(id).getDataProfile(factory));
                }
            }
        });
        reload(); //garbage hack because reasons that doesnt even work
    }
}
