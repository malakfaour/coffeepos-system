package model;

import model.base.BaseEntity;

public class Category extends BaseEntity {
    private String name;

    public Category() {}

    public Category(Integer id, String name) {
        this.setId(id);
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // ADD THIS ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    @Override
    public String toString() {
        return name;
    }
}
