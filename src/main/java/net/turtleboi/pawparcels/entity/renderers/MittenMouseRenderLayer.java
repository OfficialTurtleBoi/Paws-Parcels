package net.turtleboi.pawparcels.entity.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.turtleboi.pawparcels.entity.models.MittenMouseModel;

public class MittenMouseRenderLayer extends RenderLayer<MittenMouseRenderState, MittenMouseModel> {
    private final MittenMouseModel model;
    public MittenMouseRenderLayer(RenderLayerParent<MittenMouseRenderState, MittenMouseModel> renderer, EntityModelSet entityModelSet) {
        super(renderer);
        this.model = new MittenMouseModel(entityModelSet.bakeLayer(MittenMouseModel.MOUSE_LAYER));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, MittenMouseRenderState mittenMouseRenderState, float v, float v1) {

    }
}
