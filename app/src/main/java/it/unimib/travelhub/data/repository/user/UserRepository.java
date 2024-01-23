package it.unimib.travelhub.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import it.unimib.travelhub.data.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.travelhub.data.user.BaseUserDataRemoteDataSource;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.User;

public class UserRepository implements IUserRepository, UserResponseCallback{

    private static final String TAG = UserRepository.class.getSimpleName();

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> userPreferencesMutableLiveData;

    public UserRepository
            (
                          BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource

            ) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
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
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        return null;
    }

    @Override
    public MutableLiveData<Result> getUserPreferences(String idToken) {
        return null;
    }

    @Override
    public MutableLiveData<Result> logout() {
        return null;
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
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }

    @Override
    public void signInWithGoogle(String token) {

    }

    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
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

    }

    @Override
    public void onSuccessLogout() {

    }
}
