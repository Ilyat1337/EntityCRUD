package sample.model.Entities;

@EntityAnnotation(name = "Sword")
public class EntitySword extends EntityTool {
    @EntityAnnotation(name = "Damage")
    public int damage;

    @EntityAnnotation(name = "Cooldown time")
    public int cooldownTime;
}
