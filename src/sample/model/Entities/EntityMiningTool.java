package sample.model.Entities;

public class EntityMiningTool extends EntityTool {
    @EntityAnnotation(name = "Mining speed")
    public int miningSpeed;

    @EntityAnnotation(name = "Material ID")
    public int materialId;
}
