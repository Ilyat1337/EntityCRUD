package sample.model.Entities;

public abstract class Entity {

    @EntityAnnotation(name = "ID")
    public int id;

    @EntityAnnotation(name = "Speed")
    public double speed;

    @EntityAnnotation(name = "Texture file")
    public String textureFile;

    private int aggregationsCount = 0;

    public boolean isAggregated() {
        return aggregationsCount != 0;
    }

    public void incrementAggregations() {
        aggregationsCount++;
    }

    public void decrementAggregations() {
        aggregationsCount--;
    }

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
