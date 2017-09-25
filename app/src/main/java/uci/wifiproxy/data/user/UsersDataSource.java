package uci.wifiproxy.data.user;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by daniel on 21/09/17.
 */

public interface UsersDataSource {

    interface LoadUsersCallback{
        void onUsersLoaded(List<User> users);
        void onDataNoAvailable();
    }

    interface GetUserCallback{
        void onUserLoaded(User user);
        void onDataNoAvailable();
    }

    interface SaveUpdateUserCallback{
        void onUserSaved();
        void onUsernameAlreadyExist();
    }

    interface FilterUsersCallback{
        void onUsersFiltered(List<User> users);
        void onDataNoAvailable();
    }


    void getUsers(@NonNull LoadUsersCallback callback);

    void getUser(@NonNull String userId, @NonNull GetUserCallback callback);

    void saveUser(@NonNull User user, @NonNull SaveUpdateUserCallback callback);

    void updateUser(@NonNull User user, @NonNull SaveUpdateUserCallback callback);

    void deleteUser(@NonNull String userId);

    void deleteAllUsers();

    void filterUsers(@NonNull String usernameText, @NonNull FilterUsersCallback callback);

    void getUserByUsername(@NonNull String username, @NonNull GetUserCallback callback);
}
