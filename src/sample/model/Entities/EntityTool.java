package sample.model.Entities;

public abstract class EntityTool extends Entity {
    @EntityAnnotation(name = "Durability")
    public int durability;

    @EntityAnnotation(name = "Material type")
    public ToolMaterialType materialType;

    public enum ToolMaterialType {
        @EntityAnnotation(name = "Wood")
        WOOD,
        @EntityAnnotation(name = "Iron")
        IRON,
        @EntityAnnotation(name = "Gold")
        GOLD,
        @EntityAnnotation(name = "Diamond")
        DIAMOND
    }
}
