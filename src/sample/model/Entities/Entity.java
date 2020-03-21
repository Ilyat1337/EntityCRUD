package sample.model.Entities;

public abstract class Entity {
    Entity() {
        System.out.println("Hi!");
    }

    @EntityAnnotation(name = "ID")
    public int id;

    @EntityAnnotation(name = "Speed")
    public double speed;

    @EntityAnnotation(name = "Texture file")
    public String textureFile;

    @Override
    public String toString() {
        return String.valueOf(this.hashCode());
    }

    public String getEntityName() {
        EntityAnnotation annotation = this.getClass().getAnnotation(EntityAnnotation.class);
        if (annotation != null)
            return annotation.name();
        else
            return this.getClass().getSimpleName();
    }

}
