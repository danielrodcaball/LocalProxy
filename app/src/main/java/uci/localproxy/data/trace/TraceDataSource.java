package uci.localproxy.data.trace;

import android.support.annotation.NonNull;

import com.google.common.base.Strings;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
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

    public void filterTraces(String filter, boolean sortByConsumption, LoadTracesCallback callback) {
        RealmQuery<Trace> query = realm.where(Trace.class);
        if (!Strings.isNullOrEmpty(filter)) {
            query = query.contains(Trace.APP_NAME_FIELD, filter, Case.INSENSITIVE);
            query = query.or();
            query = query.contains(Trace.APP_SOURCE_FIELD, filter, Case.INSENSITIVE);
            query = query.or();
            query = query.contains(Trace.URL_REQUESTED_FIELD, filter, Case.INSENSITIVE);
        }
        List<Trace> traces;
        if (sortByConsumption)
            traces = query.findAllSorted(Trace.BYTES_SPENT_FIELD, Sort.DESCENDING);
        else
            traces = query.findAllSorted(Trace.DATETIME_FIELD, Sort.DESCENDING);

        if (traces.isEmpty())
            callback.onDataNoAvailable();
        else
            callback.onTracesLoaded(traces);
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
                t.setAppName(trace.getAppName());
                t.setRequestedUrl(trace.getRequestedUrl());
                t.setBytesSpent(trace.getBytesSpent());
                t.setDatetime(trace.getDatetime());
            }
        });
    }

    public void deleteAllTraces(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Trace.class).findAll().deleteAllFromRealm();
            }
        });
    }

    public interface LoadTracesCallback {
        void onTracesLoaded(List<Trace> traces);

        void onDataNoAvailable();
    }

}
