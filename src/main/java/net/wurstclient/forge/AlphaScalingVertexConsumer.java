/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormatElement;

/**
 * Forge port: wraps a chunk {@link VertexConsumer} and multiplies the alpha of
 * every vertex by a fixed factor. Used by X-Ray's opacity mode to make non-ore
 * blocks see-through, replacing the Fabric-only Indigo renderer hook
 * (AbstractBlockRenderContextMixin) from upstream.
 *
 * <p>
 * Both of Minecraft's vertex paths are covered: the "fast" 14-argument
 * {@code vertex(...)} that BakedQuads go through (including Forge's
 * {@code putBulkData}, whose default implementation ends up there), and the
 * chained {@code vertex().color()...next()} path.
 */
public final class AlphaScalingVertexConsumer implements VertexConsumer
{
	private final VertexConsumer delegate;
	private final float alphaFactor;
	
	public AlphaScalingVertexConsumer(VertexConsumer delegate,
		float alphaFactor)
	{
		this.delegate = delegate;
		this.alphaFactor = alphaFactor;
	}
	
	private int scale(int alpha)
	{
		return Math.round(alpha * alphaFactor);
	}
	
	@Override
	public void vertex(float x, float y, float z, float red, float green,
		float blue, float alpha, float u, float v, int overlay, int light,
		float normalX, float normalY, float normalZ)
	{
		delegate.vertex(x, y, z, red, green, blue, alpha * alphaFactor, u, v,
			overlay, light, normalX, normalY, normalZ);
	}
	
	@Override
	public VertexConsumer vertex(double x, double y, double z)
	{
		delegate.vertex(x, y, z);
		return this;
	}
	
	@Override
	public VertexConsumer color(int red, int green, int blue, int alpha)
	{
		delegate.color(red, green, blue, scale(alpha));
		return this;
	}
	
	@Override
	public VertexConsumer texture(float u, float v)
	{
		delegate.texture(u, v);
		return this;
	}
	
	@Override
	public VertexConsumer overlay(int u, int v)
	{
		delegate.overlay(u, v);
		return this;
	}
	
	@Override
	public VertexConsumer light(int u, int v)
	{
		delegate.light(u, v);
		return this;
	}
	
	@Override
	public VertexConsumer normal(float x, float y, float z)
	{
		delegate.normal(x, y, z);
		return this;
	}
	
	@Override
	public void next()
	{
		delegate.next();
	}
	
	@Override
	public void fixedColor(int red, int green, int blue, int alpha)
	{
		delegate.fixedColor(red, green, blue, scale(alpha));
	}
	
	@Override
	public void unfixColor()
	{
		delegate.unfixColor();
	}
	
	@Override
	public VertexConsumer misc(VertexFormatElement element, int... rawData)
	{
		delegate.misc(element, rawData);
		return this;
	}
}
