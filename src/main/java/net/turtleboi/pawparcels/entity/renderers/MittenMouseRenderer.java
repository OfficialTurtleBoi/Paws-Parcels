package net.turtleboi.pawparcels.entity.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.turtleboi.pawparcels.PawParcels;
import net.turtleboi.pawparcels.entity.entities.MittenMouseEntity;
import net.turtleboi.pawparcels.entity.models.MittenMouseModel;
import org.jspecify.annotations.Nullable;

public class MittenMouseRenderer extends LivingEntityRenderer<MittenMouseEntity, MittenMouseRenderState, MittenMouseModel> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(PawParcels.MOD_ID, "textures/entity/mitten_mouse.png");
    public MittenMouseRenderer(EntityRendererProvider.Context context) {
        super(context, new MittenMouseModel(context.bakeLayer(MittenMouseModel.MOUSE_LAYER)), 0.25f);
        this.addLayer(new MittenMouseRenderLayer(this, context.getModelSet()));
    }

    @Override
    public MittenMouseRenderState createRenderState() {
        return new MittenMouseRenderState();
    }

    @Override
    public void extractRenderState(MittenMouseEntity entity, MittenMouseRenderState state, float partialTick) {
        state.ageInTicks = entity.tickCount + partialTick;
        state.walkAnimationPos = entity.walkAnimation.position(partialTick);
        state.walkAnimationSpeed = entity.walkAnimation.speed();
        state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        state.xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        state.idle.copyFrom(entity.idleAnimationState);
        super.extractRenderState(entity, state, partialTick);
    }

    @Override
    protected @Nullable RenderType getRenderType(MittenMouseRenderState renderState, boolean visible, boolean translucent, boolean glowing) {
        return RenderTypes.entityCutout(getTextureLocation(renderState));
    }

    @Override
    protected boolean shouldShowName(MittenMouseEntity entity, double distance) {
        return entity.hasCustomName();
    }

    @Override
    public void submit(MittenMouseRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(state, poseStack, collector, cameraState);

    }

    @Override
    public Identifier getTextureLocation(MittenMouseRenderState mittenMouseRenderState) {
        return TEXTURE;
    }

}
