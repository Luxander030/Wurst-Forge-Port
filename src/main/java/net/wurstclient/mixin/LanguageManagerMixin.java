/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.wurstclient.WurstClient;
import net.wurstclient.WurstInitializer;

@Mixin(LanguageManager.class)
public abstract class LanguageManagerMixin
	implements SynchronousResourceReloader
{
	/**
	 * Forge port: initializes Wurst on the render thread. The LanguageManager
	 * is constructed in the MinecraftClient constructor right after Forge has
	 * constructed all mods (ClientModLoader.begin) and before the first
	 * resource reload, which is roughly where Fabric Loader would have run
	 * Wurst's main entrypoint. (Forge's Mixin version doesn't allow injecting
	 * into the middle of MinecraftClient's constructor directly.)
	 */
	@Inject(at = @At("RETURN"), method = "<init>(Ljava/lang/String;)V")
	private void onInit(String languageCode, CallbackInfo ci)
	{
		WurstInitializer.initializeWurst();
	}
	
	@Inject(at = @At("HEAD"),
		method = "reload(Lnet/minecraft/resource/ResourceManager;)V")
	private void onReload(ResourceManager manager, CallbackInfo ci)
	{
		// Using a mixin for this because WurstClient.initialize() runs too
		// early to call ResourceManager.registerReloader()
		WurstClient.INSTANCE.getTranslator().reload(manager);
	}
}
