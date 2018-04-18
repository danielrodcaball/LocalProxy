package uci.wifiproxy.data.profile.source;

import android.support.annotation.NonNull;

import io.realm.Realm;
import io.realm.RealmResults;
import uci.wifiproxy.data.profile.Profile;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 16/09/17.
 */

public class ProfilesLocalDataSource implements ProfilesDataSource {

    private Realm realm;

    //Prevent direct instantiation
    private ProfilesLocalDataSource() {
        realm = Realm.getDefaultInstance();
    }

    public static ProfilesLocalDataSource newInstance() {
        return new ProfilesLocalDataSource();
    }

    public void releaseResources() {
        if (realm != null) {
            realm.close();
        }
    }

    @Override
    public void getProfiles(@NonNull LoadProfilesCallback callback) {
        RealmResults<Profile> profiles = realm.where(Profile.class).findAll();
        if (!profiles.isEmpty()) {
            callback.onProfilesLoaded(profiles);
        } else {
            callback.onDataNoAvailable();
        }
    }

    @Override
    public void getProfile(@NonNull String profileId, @NonNull GetProfileCallback callback) {
        Profile profile = realm.where(Profile.class).equalTo(Profile.ID_FIELD, profileId).findFirst();
        if (profile != null) {
            callback.onProfileLoaded(profile);
        } else {
            callback.onDataNoAvailable();
        }
    }

    @Override
    public void saveProfile(@NonNull Profile profile, @NonNull SaveProfileCallback callback) {
        Profile p = realm.where(Profile.class).equalTo(Profile.NAME_FIELD, profile.getName()).findFirst();
        if (p != null) {
            callback.onProfileNameAlreadyExist();
        } else {
            realm.beginTransaction();
            Profile managedProfile = realm.copyToRealm(profile);
            realm.commitTransaction();
            callback.onProfileSaved();
        }
    }

    @Override
    public void updateProfile(@NonNull Profile profile, @NonNull UpdateProfileCallback callback) {
        Profile profileToUpdate = realm.where(Profile.class).equalTo(Profile.ID_FIELD, profile.getId()).findFirst();
        Profile profileEqualName = realm.where(Profile.class).equalTo(Profile.NAME_FIELD, profile.getName()).findFirst();
        if (profileEqualName != null && !profileEqualName.getId().equals(profileToUpdate.getId())){
            callback.onProfileNameAlreadyExist();
        }
        else {
            realm.beginTransaction();
            profileToUpdate.setName(profile.getName());
            profileToUpdate.setHost(profile.getHost());
            profileToUpdate.setBypass(profile.getBypass());
            profileToUpdate.setInPort(profile.getInPort());
            realm.commitTransaction();

            callback.onProfileUpdated();
        }
    }

    @Override
    public void deleteProfile(@NonNull String profileId) {
        realm.beginTransaction();
        realm.where(Profile.class).equalTo(Profile.ID_FIELD, profileId).findFirst().deleteFromRealm();
        realm.commitTransaction();
    }

    @Override
    public void deleteAllProfiles() {
        realm.beginTransaction();
        realm.where(Profile.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }
}
