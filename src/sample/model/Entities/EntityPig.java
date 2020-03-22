package sample.model.Entities;

public class EntityPig extends EntityAnimal {
    @EntityAnnotation(name = "Has saddle")
    public boolean hasSaddle;

    @EntityAnnotation(name = "Ridden by player")
    public EntityPlayer riddenBy;
}
