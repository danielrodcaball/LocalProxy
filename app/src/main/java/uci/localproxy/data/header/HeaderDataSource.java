package uci.localproxy.data.header;

import android.support.annotation.NonNull;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by daniel on 29/08/18.
 */

public class HeaderDataSource {

    private Realm realm;

    //Prevent direct instatntiation
    private HeaderDataSource()    {
        realm = Realm.getDefaultInstance();
    }

    public static HeaderDataSource newInstance() {
        return new HeaderDataSource();
    }

    public void releaseResources() {
        if (realm != null) {
            realm.close();
        }
    }

    public void getAllHeaders(LoadHeadersCallback callback){
        RealmResults<Header> headers = realm.where(Header.class).findAll();
        if (!headers.isEmpty()){
            callback.onHeadersLoaded(headers);
        }
        else{
            callback.onDataNoAvailable();
        }
    }

    public List<Header> getAllHeaders(){
        return realm.where(Header.class).findAll();
    }

    public void saveUpdateHeader(@NonNull Header header, @NonNull SaveUpdateHeaderCallback callback){
        Header headerToUpdate = realm.where(Header.class).equalTo(Header.ID_FIELD, header.getId()).findFirst();
        if (headerToUpdate == null){
            saveHeader(header, callback);
        }
        else{
            updateHeader(header, callback);
        }
    }

    private void updateHeader(@NonNull final Header header, @NonNull final SaveUpdateHeaderCallback callback) {
        final Header headerToUpdate = realm.where(Header.class).equalTo(Header.ID_FIELD, header.getId()).findFirst();
        Header headerEqualName = realm.where(Header.class).equalTo(Header.NAME_FIELD, header.getName()).findFirst();
        if (headerEqualName != null && !headerEqualName.getId().equals(headerToUpdate.getId())){
            callback.onHeaderNameAlreadyExist();
        }
        else{
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    headerToUpdate.setName(header.getName());
                    headerToUpdate.setValue(header.getValue());
                    callback.onHeaderSaved();
                }
            });
        }
    }

    private void saveHeader(@NonNull final Header h, @NonNull final SaveUpdateHeaderCallback callback){
        Header headerEqualName = realm.where(Header.class).equalTo(Header.NAME_FIELD, h.getName()).findFirst();
        if (headerEqualName != null){
            callback.onHeaderNameAlreadyExist();
            return;
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(h);
                callback.onHeaderSaved();
            }
        });
    }

    public void removeHeader(@NonNull final String headerId){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Header.class).equalTo(Header.ID_FIELD, headerId)
                        .findFirst()
                        .deleteFromRealm();
            }
        });
    }


    public interface LoadHeadersCallback{
        void onHeadersLoaded(List<Header> headers);
        void onDataNoAvailable();
    }

    public interface SaveUpdateHeaderCallback{
        void onHeaderSaved();
        void onHeaderNameAlreadyExist();
    }
}
