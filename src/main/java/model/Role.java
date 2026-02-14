
package model;
import model.base.BaseEntity;
public class Role extends BaseEntity {
    private String name; 
    public Role() {}
    public Role(Integer id, String name) { this.setId(id); this.name = name; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
