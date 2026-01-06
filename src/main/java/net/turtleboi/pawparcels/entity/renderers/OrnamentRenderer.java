package net.turtleboi.pawparcels.entity.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.turtleboi.pawparcels.PawParcels;
import net.turtleboi.pawparcels.entity.entities.OrnamentEntity;
import net.turtleboi.pawparcels.entity.models.OrnamentModel;

public class OrnamentRenderer extends EntityRenderer<OrnamentEntity, OrnamentRenderState> {
    private static final Identifier RED_ORNAMENT =
            Identifier.fromNamespaceAndPath(PawParcels.MOD_ID, "textures/entity/red_ornament.png");
    private static final Identifier BLUE_ORNAMENT =
            Identifier.fromNamespaceAndPath(PawParcels.MOD_ID, "textures/entity/blue_ornament.png");
    private static final Identifier GOLD_ORNAMENT =
            Identifier.fromNamespaceAndPath(PawParcels.MOD_ID, "textures/entity/gold_ornament.png");
    private static final Identifier BLACK_ORNAMENT =
            Identifier.fromNamespaceAndPath(PawParcels.MOD_ID, "textures/entity/black_ornament.png");

    private final OrnamentModel model;

    public OrnamentRenderer(EntityRendererProvider.Context context) {
        super(context);
        EntityModelSet modelSet = context.getModelSet();
        this.model = new OrnamentModel(modelSet.bakeLayer(OrnamentModel.ORNAMENT_LAYER));
    }

    @Override
    public OrnamentRenderState createRenderState() {
        return new OrnamentRenderState();
    }

    @Override
    public void extractRenderState(OrnamentEntity entity, OrnamentRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        state.xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());

        state.variantId = entity.getVariant().getVariantId();
    }

    @Override
    public void submit(OrnamentRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(state, poseStack, collector, cameraState);
        Identifier texture = switch (state.variantId) {
            case 0 -> RED_ORNAMENT;
            case 1 -> BLUE_ORNAMENT;
            case 2 -> GOLD_ORNAMENT;
            default -> BLACK_ORNAMENT;
        };

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0f));
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.yRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.xRot));
        this.model.setupAnim(state);

        collector.submitModel(
                this.model,
                state,
                poseStack,
                RenderTypes.entityCutoutNoCull(texture),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0xFFFFFFFF,
                null,
                state.outlineColor,
                null);

        poseStack.popPose();
    }

}
