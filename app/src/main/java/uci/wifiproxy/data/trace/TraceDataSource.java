package uci.wifiproxy.data.trace;

import android.support.annotation.NonNull;
import java.util.List;
import io.realm.Realm;

/**
 * Created by daniel on 18/12/17.
 */

public class TraceDataSource {

    private Realm realm;

    //Prevent direct instantiation
    private TraceDataSource(){
        realm = Realm.getDefaultInstance();
    }

    public static TraceDataSource newInstance(){
        return new TraceDataSource();
    }

    public void releaseResources(){
        if (realm != null){
            realm.close();
        }
    }

    public List<Trace> getAllTraces(){
        List<Trace> traces = realm.where(Trace.class).findAllSorted(Trace.DATE_FIELD);
        return traces;
    }

    public Trace getTraceById(@NonNull String id){
        Trace trace = realm.where(Trace.class).equalTo(Trace.ID_FIELD, id).findFirst();
        return trace;
    }

    public void saveTrace(@NonNull final Trace trace){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
            Trace t = realm.createObject(Trace.class);
            t.setId(trace.getId());
            t.setSourceApplication(trace.getSourceApplication());
            t.setRequestedUrl(trace.getRequestedUrl());
            t.setBytesSpent(trace.getBytesSpent());
            t.setDate(trace.getDate());
            }
        }, new Realm.Transaction.OnError(){

            @Override
            public void onError(Throwable error) {
//                Timber.e(throwable, "Failed to save data.");
            }
        });

//        realm.beginTransaction();
//        realm.copyToRealm(trace);
//        realm.commitTransaction();
    }
}
