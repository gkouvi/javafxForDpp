package gr.uoi.dit.master2025.gkouvas.dppclient.model;

import java.util.List;

public class UserRow {
    private Long id;
    private String username;
    private boolean enabled;
    private List<RoleDto> roles;

    public UserRow() {}

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public boolean isEnabled() { return enabled; }
    public List<RoleDto> getRoles() { return roles; }

    public String getRolesAsString() {
        if (roles == null) return "";
        return roles.stream()
                .map(RoleDto::name)
                .reduce((a,b) -> a + ", " + b)
                .orElse("");
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}


