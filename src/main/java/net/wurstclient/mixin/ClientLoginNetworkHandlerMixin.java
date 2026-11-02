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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.wurstclient.WurstClient;

/**
 * Replaces Fabric API's {@code ClientLoginConnectionEvents.INIT}, which
 * NoChatReportsOtf relied on.
 */
@Mixin(ClientLoginNetworkHandler.class)
public abstract class ClientLoginNetworkHandlerMixin
{
	@Inject(at = @At("TAIL"), method = "<init>")
	private void onInit(CallbackInfo ci)
	{
		WurstClient.INSTANCE.getOtfs().noChatReportsOtf.onLoginStart(
			(ClientLoginNetworkHandler)(Object)this,
			MinecraftClient.getInstance());
	}
}
