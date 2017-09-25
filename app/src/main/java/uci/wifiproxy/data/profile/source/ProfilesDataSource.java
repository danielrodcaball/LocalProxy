package uci.wifiproxy.data.profile.source;

import android.support.annotation.NonNull;

import java.util.List;

import uci.wifiproxy.data.profile.Profile;

/**
 * Created by daniel on 16/09/17.
 */

public interface ProfilesDataSource {

    interface LoadProfilesCallback{
        void onProfilesLoaded(List<Profile> profiles);
        void onDataNoAvailable();
    }

    interface GetProfileCallback{
        void onProfileLoaded(Profile profile);
        void onDataNoAvailable();
    }

    interface SaveProfileCallback{
        void onProfileSaved();
        void onProfileNameAlreadyExist();
    }

    interface UpdateProfileCallback{
        void onProfileUpdated();
        void onProfileNameAlreadyExist();
    }

    void getProfiles(@NonNull LoadProfilesCallback callback);

    void getProfile(@NonNull String profileId, @NonNull GetProfileCallback callback);

    void saveProfile(@NonNull Profile profile, @NonNull SaveProfileCallback callback);

    void updateProfile(@NonNull Profile profile,
                       @NonNull UpdateProfileCallback callback);

    void deleteProfile(@NonNull String profileId);

    void deleteAllProfiles();
}
