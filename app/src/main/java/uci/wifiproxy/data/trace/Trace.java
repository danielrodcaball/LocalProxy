package uci.wifiproxy.data.trace;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by daniel on 18/12/17.
 */

public class Trace extends RealmObject {

    public static final String UNKNOWN_APP_NAME = "unknown";

    public static final String ID_FIELD = "id";
    public static final String APP_SOURCE_FIELD = "sourceApplication";
    public static final String URL_REQUESTED_FIELD = "requestedUrl";
    public static final String BYTES_SPENT_FIELD = "bytesSpent";
    public static final String DATETIME_FIELD = "datetime";
    public static final String APP_NAME_FIELD = "appName";

    @PrimaryKey
    private String id;

    @Required
    private String sourceApplication;

    @Required
    private String appName;

    @Required
    private String requestedUrl;

    private long bytesSpent;

    private long datetime;

    public static Trace newTrace(String sourceApplication, String appName, String requestedUrl, long bytesSpent,
                                 long datetime){
        Trace trace = new Trace();
        trace.setId(UUID.randomUUID().toString());
        trace.setSourceApplication(sourceApplication);
        trace.setAppName(appName);
        trace.setRequestedUrl(requestedUrl);
        trace.setBytesSpent(bytesSpent);
        trace.setDatetime(datetime);
        return trace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceApplication() {
        return sourceApplication;
    }

    public void setSourceApplication(String sourceApplication) {
        this.sourceApplication = sourceApplication;
    }

    public String getRequestedUrl() {
        return requestedUrl;
    }

    public void setRequestedUrl(String requestedUrl) {
        this.requestedUrl = requestedUrl;
    }

    public long getBytesSpent() {
        return bytesSpent;
    }

    public void setBytesSpent(long bytesSpent) {
        this.bytesSpent = bytesSpent;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
