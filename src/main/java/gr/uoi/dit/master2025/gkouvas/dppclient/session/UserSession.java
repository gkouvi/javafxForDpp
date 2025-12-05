/*
package gr.uoi.dit.master2025.gkouvas.dppclient.session;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UserSession {

    private static String token;
    private static String username;
    private static Set<String> roles = new HashSet<>();

    public static void setToken(String t) {
        token = t;
    }

    public static String getToken() {
        return token;
    }

    public static void setUsername(String u) { username = u; }

    public static String getUsername() { return username; }

    public static boolean isLoggedIn() {
        return token != null && !token.isEmpty();
    }
    public static void setRoles(Collection<String> r) {
        roles.clear();
        roles.addAll(r);
    }

    public static boolean hasRole(String r) {
        return roles.contains("ROLE_" + r);
    }

    public static Set<String> getRoles() {
        return roles;
    }
    public static boolean isAdmin() { return hasRole("ADMIN"); }
    public static boolean isSupervisor() { return hasRole("SUPERVISOR"); }
    public static boolean isTechnician() { return hasRole("TECHNICIAN"); }

    public static String getBaseUrl() {
        return "https://192.168.0.105:8443";
    }


}

*/
package gr.uoi.dit.master2025.gkouvas.dppclient.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public class UserSession {

    private static String token;
    private static String username;
    private static final Set<String> roles = new HashSet<>();

    public static void setToken(String t) {
        token = t;
        decodeRolesFromToken(t);
    }
    public static boolean isLoggedIn() {
        return token != null && !token.isEmpty();
    }

    public static String getToken() {
        return token;
    }

    public static void setUsername(String u) {
        username = u;
    }

    public static String getUsername() {
        return username;
    }

    public static boolean isAdmin() {
        return roles.contains("ROLE_ADMIN");
    }

    public static boolean isSupervisor() {
        return roles.contains("ROLE_SUPERVISOR");
    }

    public static boolean isTechnician() {
        return roles.contains("ROLE_TECHNICIAN");
    }

    public static Set<String> getRoles() {
        return roles;
    }

    private static void decodeRolesFromToken(String jwt) {
        try {
            roles.clear();

            String[] parts = jwt.split("\\.");
            if (parts.length != 3) return;

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(payloadJson);

            JsonNode rolesArray = node.get("roles");
            if (rolesArray != null && rolesArray.isArray()) {
                for (JsonNode r : rolesArray) {
                    String roleName = r.get("authority").asText();
                    roles.add(roleName);
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getBaseUrl() {
        return "https://192.168.0.105:8443";
    }
}
