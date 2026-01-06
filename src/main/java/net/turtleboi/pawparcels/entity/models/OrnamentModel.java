package net.turtleboi.pawparcels.entity.models;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;
import net.turtleboi.pawparcels.PawParcels;
import net.turtleboi.pawparcels.entity.renderers.OrnamentRenderState;

public class OrnamentModel extends EntityModel<OrnamentRenderState> {
    public static final ModelLayerLocation ORNAMENT_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(PawParcels.MOD_ID, "ornament"), "main");
    private final ModelPart ornament;

    public OrnamentModel(ModelPart root) {
        super(root);
        this.ornament = root.getChild("ornament");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partdefinition = meshDefinition.getRoot();

        PartDefinition ornament = partdefinition.addOrReplaceChild("ornament", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -5.0F, -2.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(20, 6).addBox(-2.0F, -6.0F, -1.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 0.0F, -0.5F));

        PartDefinition hanger2_r1 = ornament.addOrReplaceChild("hanger2_r1", CubeListBuilder.create().texOffs(23, 1).addBox(0.0F, -1.0F, -1.5F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -7.0F, 0.5F, 0.0F, -2.3562F, 0.0F));
        PartDefinition hanger1_r1 = ornament.addOrReplaceChild("hanger1_r1", CubeListBuilder.create().texOffs(23, 1).addBox(0.0F, -1.0F, -1.5F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -7.0F, 0.5F, 0.0F, -0.7854F, 0.0F));

        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    @Override
    public void setupAnim(OrnamentRenderState renderState) {
        super.setupAnim(renderState);
    }
}
