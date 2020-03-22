package sample.model.Entities;

public abstract class EntityMob extends EntityCreature {
    @EntityAnnotation(name = "Attack strength")
    public int attackStrength;

    @EntityAnnotation(name = "Attack target")
    public EntityLiving attackTarget;
}
