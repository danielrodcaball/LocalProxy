package uci.localproxy.data.header;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by daniel on 29/08/18.
 */

public class Header extends RealmObject {

    public static final String ID_FIELD = "id";
    public static final String NAME_FIELD = "name";
    public static final String VALUE_FIELD = "value";

    @PrimaryKey
    private String id;

    @Required
    private String name;

    private String value;

    public static Header newHeader(String name, String value){
        Header header = new Header();
        header.setId(UUID.randomUUID().toString());
        header.setName(name);
        header.setValue(value);
        return header;
    }

    public static Header newHeader(String headerId, String name, String value){
        Header header = new Header();
        header.setId(headerId);
        header.setName(name);
        header.setValue(value);
        return header;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name + " : " + value;
    }
}
