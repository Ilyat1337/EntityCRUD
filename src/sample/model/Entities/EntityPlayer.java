package sample.model.Entities;

public class EntityPlayer extends EntityLiving {
    @EntityAnnotation(name = "Username")
    public String username;

    @EntityAnnotation(name = "Dimension")
    public DimensionType dimension;

    @EntityAnnotation(name = "Tool")
    public EntityTool tool;

    public String getEntityName() {
        return super.getEntityName() + " (" + username + ")";
    }

    public enum DimensionType {
        @EntityAnnotation(name = "Overworld")
        OVERWORLD,
        @EntityAnnotation(name = "Nether")
        NETHER,
        @EntityAnnotation(name = "End")
        END
    }
}
