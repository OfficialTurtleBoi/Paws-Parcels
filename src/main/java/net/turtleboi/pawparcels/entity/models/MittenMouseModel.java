package net.turtleboi.pawparcels.entity.models;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.entity.animation.json.AnimationHolder;
import net.turtleboi.pawparcels.PawParcels;
import net.turtleboi.pawparcels.entity.renderers.MittenMouseRenderState;

public class MittenMouseModel extends EntityModel<MittenMouseRenderState> {
    public static final ModelLayerLocation MOUSE_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(PawParcels.MOD_ID, "mitten_mouse"), "main");
    private final ModelPart mitten_mouse;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart right_hind_leg;
    private final ModelPart left_hind_leg;
    private final ModelPart tail;

    public MittenMouseModel(ModelPart root) {
        super(root);
        this.mitten_mouse = root.getChild("mitten_mouse");
        this.head = this.mitten_mouse.getChild("head");
        this.body = this.mitten_mouse.getChild("body");
        this.right_arm = this.body.getChild("right_arm");
        this.left_arm = this.body.getChild("left_arm");
        this.right_hind_leg = this.body.getChild("right_hind_leg");
        this.left_hind_leg = this.body.getChild("left_hind_leg");
        this.tail = this.body.getChild("tail");
        this.mouseIdle = IDLE_ANIMATION.get().bake(root);
        this.mouseWalking = WALKING_ANIMATION.get().bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition mitten_mouse = partdefinition.addOrReplaceChild("mitten_mouse", CubeListBuilder.create(), PartPose.offset(-0.5F, 22.5F, 0.25F));

        PartDefinition head = mitten_mouse.addOrReplaceChild("head", CubeListBuilder.create().texOffs(12, 1).addBox(-4.5F, -4.0F, -0.5F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(12, 1).mirror().addBox(0.5F, -4.0F, -0.5F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 0).addBox(-1.5F, -1.0F, -3.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, -1.25F));

        PartDefinition body = mitten_mouse.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 5).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.75F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 12).addBox(-1.0F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 1.0F, -1.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 12).addBox(0.0F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 1.0F, -1.0F));

        PartDefinition right_hind_leg = body.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(14, 8).addBox(-1.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(7, 13).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 0.5F, 1.0F));

        PartDefinition left_hind_leg = body.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(14, 8).addBox(0.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(7, 13).addBox(0.0F, 0.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 0.5F, 1.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(20, -3).addBox(0.0F, -3.5F, 0.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public static final AnimationHolder IDLE_ANIMATION =
            Model.getAnimation(Identifier.fromNamespaceAndPath(PawParcels.MOD_ID, "mouse_idle"));
    private final KeyframeAnimation mouseIdle;

    public static final AnimationHolder WALKING_ANIMATION =
            Model.getAnimation(Identifier.fromNamespaceAndPath(PawParcels.MOD_ID, "mouse_walking"));
    private final KeyframeAnimation mouseWalking;

    @Override
    public void setupAnim(MittenMouseRenderState renderState) {
        super.setupAnim(renderState);
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(renderState.yRot, renderState.xRot);
        this.mouseIdle.apply(renderState.idle, renderState.ageInTicks);

        this.mouseWalking.applyWalk(
                renderState.walkAnimationPos,
                renderState.walkAnimationSpeed,
                2, 2);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

        this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
    }
}
