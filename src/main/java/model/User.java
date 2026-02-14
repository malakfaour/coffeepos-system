
package model;
import model.base.BaseEntity;

public class User extends BaseEntity {
    private String username;
    private String passwordHash;
    private String fullName;
    private Role role;
    private boolean active = true;

 
    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String ph) { this.passwordHash = ph; }
    public String getFullName() { return fullName; }
    public void setFullName(String fn) { this.fullName = fn; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
