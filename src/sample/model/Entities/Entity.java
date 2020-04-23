package sample.model.Entities;

import java.io.Serializable;

public abstract class Entity implements Serializable {

    @EntityAnnotation(name = "ID")
    public int id;

    @EntityAnnotation(name = "Speed")
    public double speed;

    @EntityAnnotation(name = "Texture file")
    public String textureFile;

    private int aggregationsCount = 0;

    private int hashCode = 0;

    public boolean isAggregated() {
        return aggregationsCount != 0;
    }

    public void incrementAggregations() {
        aggregationsCount++;
    }

    public void decrementAggregations() {
        aggregationsCount--;
    }

    public int getAggregationsCount() {
        return aggregationsCount;
    }

    public void setAggregationsCount(int aggregationsCount) {
        this.aggregationsCount = aggregationsCount;
    }

    public int getHashCode() {
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    @Override
    public String toString() {
        if (hashCode == 0)
            hashCode = this.hashCode();
        return String.valueOf(hashCode);
    }

    public String getEntityName() {
        EntityAnnotation annotation = this.getClass().getAnnotation(EntityAnnotation.class);
        if (annotation != null)
            return annotation.name();
        else
            return this.getClass().getSimpleName();
    }

}
