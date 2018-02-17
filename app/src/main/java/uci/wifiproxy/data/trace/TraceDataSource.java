package uci.wifiproxy.data.trace;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import io.realm.Realm;
import io.realm.Sort;

/**
 * Created by daniel on 18/12/17.
 */

public class TraceDataSource {

    private Realm realm;

    //Prevent direct instantiation
    private TraceDataSource() {
        realm = Realm.getDefaultInstance();
    }

    public static TraceDataSource newInstance() {
        return new TraceDataSource();
    }

    public void releaseResources() {
        if (realm != null) {
            realm.close();
        }
    }

    public void getAllTraces(LoadTracesCallback callback) {
        List<Trace> traces = realm.where(Trace.class).findAllSorted(Trace.DATETIME_FIELD, Sort.DESCENDING);
        if (traces.isEmpty()) {
            callback.onDataNoAvailable();
        } else {
            callback.onTracesLoaded(traces);
        }
    }

    public Trace getTraceById(@NonNull String id) {
        Trace trace = realm.where(Trace.class).equalTo(Trace.ID_FIELD, id).findFirst();
        return trace;
    }

    public void saveTrace(@NonNull final Trace trace) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Trace t = realm.createObject(Trace.class, trace.getId());
                t.setSourceApplication(trace.getSourceApplication());
                t.setRequestedUrl(trace.getRequestedUrl());
                t.setBytesSpent(trace.getBytesSpent());
                t.setDatetime(trace.getDatetime());
            }
        });
    }

    public interface LoadTracesCallback {
        void onTracesLoaded(List<Trace> traces);

        void onDataNoAvailable();
    }

}
