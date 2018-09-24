package uci.localproxy.data.profile;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by daniel on 16/09/17.
 */

public class Profile extends RealmObject {

    public static final String ID_FIELD = "id";
    public static final String NAME_FIELD = "name";

    @PrimaryKey
    private String id;

    @Required
    private String name;

    @Required
    private String host;

    private int inPort;

    private String bypass;

    public static Profile newProfile(String name, String server,
                                     int inPort, String bypass){
        Profile p = new Profile();
        p.setId(UUID.randomUUID().toString());
        p.setName(name);
        p.setHost(server);
        p.setInPort(inPort);
        p.setBypass(bypass);

        return p;
    }

    public static Profile newProfile(String profileId, String name,
                                     String server, int inPort, String bypass){
        Profile p = new Profile();
        p.setId(profileId);
        p.setName(name);
        p.setHost(server);
        p.setInPort(inPort);
        p.setBypass(bypass);

        return p;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getInPort() {
        return inPort;
    }

    public void setInPort(int inPort) {
        this.inPort = inPort;
    }

    public String getBypass() {
        return bypass;
    }

    public void setBypass(String bypass) {
        this.bypass = bypass;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
