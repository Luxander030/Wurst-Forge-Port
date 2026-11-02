/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge;

import java.util.stream.Stream;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.language.IModInfo;

/**
 * Replaces the bits of {@code FabricLoader} that Wurst used.
 */
public final class ForgePlatform
{
	private ForgePlatform()
	{}
	
	public static boolean isModLoaded(String modId)
	{
		ModList modList = ModList.get();
		if(modList != null)
			return modList.isLoaded(modId);
		
		// ModList isn't ready yet during very early startup
		return FMLLoader.getLoadingModList().getModFileById(modId) != null;
	}
	
	public static String getModVersion(String modId)
	{
		ModList modList = ModList.get();
		if(modList == null)
			return null;
		
		return modList.getModContainerById(modId)
			.map(c -> c.getModInfo().getVersion().toString()).orElse(null);
	}
	
	public static Stream<String> getAllModIds()
	{
		return ModList.get().getMods().stream().map(IModInfo::getModId);
	}
	
	public static boolean isDevelopmentEnvironment()
	{
		return !FMLLoader.isProduction();
	}
}
