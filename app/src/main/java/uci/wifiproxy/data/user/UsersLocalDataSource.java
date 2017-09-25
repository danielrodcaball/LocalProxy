package uci.wifiproxy.data.user;

import android.support.annotation.NonNull;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by daniel on 21/09/17.
 */

public class UsersLocalDataSource implements UsersDataSource {

    private Realm realm;

    //Prevent direct instatntiation
    private UsersLocalDataSource()    {
        realm = Realm.getDefaultInstance();
    }

    public static UsersLocalDataSource newInstance() {
        return new UsersLocalDataSource();
    }

    public void releaseResources() {
        if (realm != null) {
            realm.close();
        }
    }

    @Override
    public void getUsers(@NonNull LoadUsersCallback callback) {
        RealmResults<User> users = realm.where(User.class).findAll();
        if (!users.isEmpty()){
            callback.onUsersLoaded(users);
        }
        else{
            callback.onDataNoAvailable();
        }
    }

    @Override
    public void getUser(@NonNull String userId, @NonNull GetUserCallback callback) {
        User user = realm.where(User.class).equalTo(User.ID_FIELD, userId).findFirst();
        if (user != null){
            callback.onUserLoaded(user);
        }
        else{
            callback.onDataNoAvailable();
        }
    }

    @Override
    public void saveUser(@NonNull User user, @NonNull SaveUpdateUserCallback callback) {
        User userEqualUsername = realm.where(User.class).equalTo(User.USERNAME_FIELD, user.getUsername())
                .findFirst();
        if (userEqualUsername != null){
            callback.onUsernameAlreadyExist();
        }
        else{
            realm.beginTransaction();
            realm.copyToRealm(user);
            realm.commitTransaction();
            callback.onUserSaved();
        }
    }

    @Override
    public void updateUser(@NonNull User user, @NonNull SaveUpdateUserCallback callback) {
        User userToUpdate = realm.where(User.class).equalTo(User.ID_FIELD, user.getId()).findFirst();
        User userEqualName = realm.where(User.class).equalTo(User.USERNAME_FIELD, user.getUsername()).findFirst();
        if (userEqualName != null && !userEqualName.getId().equals(userToUpdate.getId())){
            callback.onUsernameAlreadyExist();
        }
        else{
            realm.beginTransaction();
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setPassword(user.getPassword());
            realm.commitTransaction();
            callback.onUserSaved();
        }
    }

    @Override
    public void deleteUser(@NonNull String userId) {
        realm.beginTransaction();
        realm.where(User.class).equalTo(User.ID_FIELD, userId).findFirst().deleteFromRealm();
        realm.commitTransaction();
    }

    @Override
    public void deleteAllUsers() {
        realm.beginTransaction();
        realm.where(User.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    @Override
    public void filterUsers(@NonNull String usernameText, @NonNull FilterUsersCallback callback) {
        List<User> filteredUsers = realm.where(User.class)
                .beginsWith(User.USERNAME_FIELD, usernameText).findAll();
        if (!filteredUsers.isEmpty()){
            callback.onUsersFiltered(filteredUsers);
        }
        else {
            callback.onDataNoAvailable();
        }
    }

    @Override
    public void getUserByUsername(@NonNull String username, @NonNull GetUserCallback callback) {
        User user = realm.where(User.class).equalTo(User.USERNAME_FIELD, username).findFirst();
        if (user == null){
            callback.onDataNoAvailable();
        }
        else{
            callback.onUserLoaded(user);
        }
    }
}
