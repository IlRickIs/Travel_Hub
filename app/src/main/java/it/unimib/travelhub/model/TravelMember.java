package it.unimib.travelhub.model;

public class TravelMember {
    private String username;
    private Role role;
    public enum Role {MEMBER, SUPPORTER, CREATOR}

    public TravelMember() {
        this.username = null;
        this.role = Role.MEMBER;
    }

    public TravelMember(String username, Role role) {
        this.username = username;
        this.role = role;
    }


    public TravelMember(String username) {
        this.username = username;
        this.role = Role.MEMBER;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setRole() {
        this.role = Role.MEMBER;
    }

}
