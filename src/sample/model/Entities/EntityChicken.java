package sample.model.Entities;

public class EntityChicken extends EntityAnimal{
    @EntityAnnotation(name = "Time until egg")
    public int timeUntilNextEgg;
}
