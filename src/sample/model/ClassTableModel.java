package sample.model;

import sample.model.Entities.Entity;

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
}
