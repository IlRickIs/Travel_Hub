package it.unimib.travelhub.data.repository.user;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import it.unimib.travelhub.data.source.BaseRemoteFileStorageSource;
import it.unimib.travelhub.data.source.RemoteFileStorageCallback;
import it.unimib.travelhub.data.source.RemoteFileStorageSource;
import it.unimib.travelhub.data.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.travelhub.data.user.BaseUserDataRemoteDataSource;
import it.unimib.travelhub.data.user.UserAuthenticationRemoteDataSource;
import it.unimib.travelhub.data.user.UserDataRemoteDataSource;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.User;

public class UserRepository implements IUserRepository, UserResponseCallback, RemoteFileStorageCallback {

    private static final String TAG = UserRepository.class.getSimpleName();

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final BaseRemoteFileStorageSource remoteFileStorageSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> userPreferencesMutableLiveData;

    public UserRepository
            (BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
             BaseUserDataRemoteDataSource userDataRemoteDataSource,
             BaseRemoteFileStorageSource remoteFileStorageSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.remoteFileStorageSource = remoteFileStorageSource;
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
        this.remoteFileStorageSource.setRemoteFileStorageCallback(this);
        this.userMutableLiveData = new MutableLiveData<>();
        this.userPreferencesMutableLiveData = new MutableLiveData<>();
    }

    @Override
    public MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            signUp(email, password);
        }
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUser(String username, String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            isUsernameTaken(username, email, password);
        }
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getGoogleUser(User user) {
        signInWithGoogle(user);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserPreferences(String idToken) {
        return null;
    }

    @Override
    public MutableLiveData<Result> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }

    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public void signUp(String email, String password) {
        userRemoteDataSource.signUp(email, password);
    }

    @Override
    public void signUp(String username, String email, String password) {
            userRemoteDataSource.signUp(username, email, password);
    }


    public void isUsernameTaken(String username, String email, String password){
        userDataRemoteDataSource.isUsernameTaken(username, email, password);
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }

    @Override
    public void signInWithGoogle(User user) {
        userRemoteDataSource.signInWithGoogle(user);
    }

    @Override
    public MutableLiveData<Result> isUserRegistered(String username, UserDataRemoteDataSource.UsernameCheckCallback userDataCallback) {
        userDataRemoteDataSource.isUserRegistered(username, userDataCallback);
        return userMutableLiveData;
    }
    @Override
    public void updateUserData(User user, UserDataRemoteDataSource.UserCallback userCallback) {
        userDataRemoteDataSource.updateUserData(user, userCallback);
    }

    @Override
    public void uploadProfileImage(String remotePath, Uri imageUri, RemoteFileStorageSource.uploadCallback uploadCallback) {
        remoteFileStorageSource.upload(remotePath, imageUri, uploadCallback);
    }

    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            Log.d(TAG, "user: " + user.toString());
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(user);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromGettingUserPreferences() {

    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessLogout() {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(null);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void isGoogleUserAlreadyRegistered(User user, UserAuthenticationRemoteDataSource.GoogleUserCallback callback) {
        userRemoteDataSource.isGoogleUserRegistered(user, callback);
    }

    @Override
    public void onSuccessProfileImageUpload(String downloadUrl, User user) {

    }

    @Override
    public void onSuccessDownload(String filePath) {

    }

    @Override
    public void onSuccessDelete() {

    }

    @Override
    public void onFailure(Exception exception) {

    }
}
