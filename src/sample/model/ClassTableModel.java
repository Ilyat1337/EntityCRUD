package sample.model;

import sample.model.Entities.Entity;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ClassTableModel {

    private ArrayList<Entity> entities;

    public ClassTableModel() {
        entities = new ArrayList<>();
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(int entityIndex) {
        entities.remove(entityIndex);
    }

    public void handleDeleteClass(int selectedIndex) {
        Entity entityToDelete = entities.get(selectedIndex);
        if (entityToDelete.isAggregated())
            nullParentLinks(entityToDelete);
        decrementAggregationsCount(entityToDelete);
        entities.remove(selectedIndex);
    }

    private void nullParentLinks(Entity entityToDelete) {
        for (Entity entity : entities) {
            Field[] fields = entity.getClass().getFields();
            for (Field field : fields) {
                try {
                    if (field.getType().isAssignableFrom(entityToDelete.getClass()))
                        field.set(entity, null);
                } catch (Exception e) {
                }
            }
        }
    }

    private void decrementAggregationsCount(Entity entityToDelete) {
        Field[] fields = entityToDelete.getClass().getFields();
        for (Field field : fields) {
            try {
                if (Entity.class.isAssignableFrom(field.getType()))
                    ((Entity) field.get(entityToDelete)).decrementAggregations();
            } catch (Exception e) {
            }
        }
    }
}
