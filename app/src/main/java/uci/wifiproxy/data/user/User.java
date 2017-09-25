package uci.wifiproxy.data.user;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import uci.wifiproxy.util.Security.Encripter;

/**
 * Created by daniel on 20/09/17.
 */

public class User extends RealmObject {

    public static final String ID_FIELD = "id";
    public static final String USERNAME_FIELD = "username";
    public static final String PASSWORD_FIELD = "password";

    @PrimaryKey
    private String id;

    @Required
    private String username;

    private String password;

    public static User newUser(String username, String password){
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    public static User newUser(String userId, String username, String password){
        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return Encripter.decrypt(password);
    }

    public void setPassword(String password) {
        this.password = Encripter.encrypt(password);
    }

    @Override
    public String toString() {
        return username;
    }
}
