package it.unimib.travelhub.model;

import java.io.Serializable;

public class TravelMember extends User implements Serializable {
    private Role role;
    public enum Role {MEMBER, SUPPORTER, CREATOR}

    public TravelMember() {
        super();
        this.role = Role.MEMBER;
    }

    public TravelMember(Role role) {
        super();
        this.role = role;
    }

    public TravelMember(String username){
        super(username);
        this.role = Role.MEMBER;
    }

    public TravelMember(String username, String id, Role role){
        super(username, id);
        this.role = role;
    }
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setRole() {
        this.role = Role.MEMBER;
    }
}
