/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient;

import net.minecraftforge.fml.common.Mod;
import net.wurstclient.test.WurstE2ETestClient;

/**
 * Forge entrypoint.
 *
 * <p>
 * On Fabric, Wurst initializes from the "main" entrypoint, which Fabric Loader
 * runs on the render thread inside the {@code MinecraftClient} constructor.
 * Forge runs {@code @Mod} constructors on parallel worker threads instead, so
 * the actual {@link WurstClient#initialize()} call is made by
 * {@code LanguageManagerMixin} when Minecraft constructs its LanguageManager,
 * right after Forge has finished constructing mods. That puts it back on the
 * render thread and before the first resource reload (which Wurst's
 * translator hooks into).
 */
@Mod("wurst")
public final class WurstInitializer
{
	private static boolean initialized;
	
	public WurstInitializer()
	{
		new WurstE2ETestClient().onInitialize();
	}
	
	public static void initializeWurst()
	{
		if(initialized)
			return;
		
		WurstClient.INSTANCE.initialize();
		initialized = true;
	}
}
