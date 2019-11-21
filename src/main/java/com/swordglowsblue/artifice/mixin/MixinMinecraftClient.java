package com.swordglowsblue.artifice.mixin;

import com.swordglowsblue.artifice.api.Artifice;
import com.swordglowsblue.artifice.api.virtualpack.ArtificeResourcePackProfile;
import org.apache.logging.log4j.LogManager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Shadow private ResourcePackManager<ClientResourcePackProfile> resourcePackManager;
    
    @Shadow public abstract CompletableFuture<Void> reloadResources();
    
    @Inject(method = "<init>(Lnet/minecraft/client/RunArgs;)V", at = @At("RETURN"))
    private void registerPackCreator(CallbackInfo cbi) {
        LogManager.getLogger().info("TESTCLIENT");
        this.resourcePackManager.registerProvider(new ResourcePackProvider() {
            public <T extends ResourcePackProfile> void register(Map<String, T> packs, ResourcePackProfile.Factory<T> factory) {
                for(Identifier id : Artifice.ASSETS.getIds()) {
                    LogManager.getLogger().info("asset "+id);
                    packs.put(id.toString(), (T)Artifice.ASSETS.get(id).getAssetsProfile(factory));
                }
            }
        });
    
        this.resourcePackManager.getDisabledProfiles().forEach(c -> {
            if(c instanceof ArtificeResourcePackProfile && !((ArtificeResourcePackProfile)c).isOptional())
                this.resourcePackManager.getEnabledProfiles().add(c);
        });
        reloadResources(); //garbage hack because reasons
    }
}
