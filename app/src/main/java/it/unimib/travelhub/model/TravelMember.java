package it.unimib.travelhub.model;

public class TravelMember extends User {
    private String id;
    private Role role;
    public enum Role {MEMBER, SUPPORTER, CREATOR}

    public TravelMember() {
        this.id = null;
        this.role = Role.MEMBER;
    }

    public TravelMember(String id, Role role) {
        this.id = id;
        this.role = role;
    }

    public TravelMember(String id) {
        this.id = id;
        this.role = Role.MEMBER;
    }

    public String getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setRole() {
        this.role = Role.MEMBER;
    }
}
