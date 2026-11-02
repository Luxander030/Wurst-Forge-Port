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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import net.wurstclient.WurstClient;
import net.wurstclient.forge.AlphaScalingVertexConsumer;
import net.wurstclient.hacks.XRayHack;

/**
 * Forge port: makes X-Ray's opacity mode work with Forge's chunk builder.
 *
 * <p>
 * On Fabric, opacity mode relies on RenderLayersMixin (move every block to the
 * translucent layer) plus a hook into Fabric API's Indigo renderer that lowers
 * the alpha of non-ore quads. Forge picks render layers per model through
 * {@code BakedModel.getRenderTypes()} instead of
 * {@code RenderLayers.getBlockLayer()}, and has no Indigo, so both parts are
 * done here, in the chunk rebuild task itself.
 */
@Mixin(targets = "net.minecraft.client.render.chunk.ChunkBuilder$BuiltChunk$RebuildTask")
public abstract class ChunkRebuildTaskMixin
{
	/**
	 * Puts all blocks on the translucent layer if Opacity X-Ray is enabled.
	 */
	@WrapOperation(at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/render/model/BakedModel;getRenderTypes(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/random/Random;Lnet/minecraftforge/client/model/data/ModelData;)Lnet/minecraftforge/client/ChunkRenderTypeSet;"),
		method = "render")
	private ChunkRenderTypeSet wrapGetRenderTypes(BakedModel model,
		BlockState state, Random random, ModelData modelData,
		Operation<ChunkRenderTypeSet> original)
	{
		if(WurstClient.INSTANCE.getHax().xRayHack.isOpacityMode())
			return ChunkRenderTypeSet.of(RenderLayer.getTranslucent());
		
		return original.call(model, state, random, modelData);
	}
	
	/**
	 * Lowers the alpha of every vertex of non-ore blocks if Opacity X-Ray is
	 * enabled.
	 */
	@WrapOperation(at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/render/block/BlockRenderManager;renderBatched(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/render/RenderLayer;)V"),
		method = "render")
	private void wrapRenderBatched(BlockRenderManager blockRenderManager,
		BlockState state, BlockPos pos, BlockRenderView world,
		MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull,
		Random random, ModelData modelData, RenderLayer renderLayer,
		Operation<Void> original)
	{
		XRayHack xray = WurstClient.INSTANCE.getHax().xRayHack;
		if(xray.isOpacityMode() && !xray.isVisible(state.getBlock(), pos))
		{
			float alpha = (xray.getOpacityColorMask() >>> 24) / 255F;
			vertexConsumer =
				new AlphaScalingVertexConsumer(vertexConsumer, alpha);
		}
		
		original.call(blockRenderManager, state, pos, world, matrices,
			vertexConsumer, cull, random, modelData, renderLayer);
	}
}
