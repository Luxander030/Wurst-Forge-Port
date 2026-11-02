/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;

/**
 * Replacement for Fabric API's
 * {@code net.wurstclient.forge.Screens}.
 */
public final class Screens
{
	private Screens()
	{}
	
	/**
	 * Returns a snapshot of all clickable widgets that the given screen
	 * renders. Unlike Fabric API's version, the returned list is a copy, so
	 * adding to it does not add a button to the screen.
	 */
	public static List<ClickableWidget> getButtons(Screen screen)
	{
		ArrayList<ClickableWidget> buttons = new ArrayList<>();
		for(Drawable drawable : screen.drawables)
			if(drawable instanceof ClickableWidget widget)
				buttons.add(widget);
			
		return buttons;
	}
}
