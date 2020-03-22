package sample.model.Entities;

public abstract class EntityAnimal extends EntityCreature {
    @EntityAnnotation(name = "Time since breed")
    public int timeSinceBreed;
}
