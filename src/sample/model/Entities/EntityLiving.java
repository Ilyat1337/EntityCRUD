package sample.model.Entities;

public abstract class EntityLiving extends Entity {
    @EntityAnnotation(name = "Health")
    public int health;

    @EntityAnnotation(name = "Is attacked")
    public boolean isAttacked;
}
