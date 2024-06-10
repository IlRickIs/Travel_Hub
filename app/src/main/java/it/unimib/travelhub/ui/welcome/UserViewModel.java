package it.unimib.travelhub.ui.welcome;

import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import it.unimib.travelhub.crypto_util.DataEncryptionUtil;
import it.unimib.travelhub.data.repository.user.IUserRepository;
import it.unimib.travelhub.data.user.UserDataRemoteDataSource;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.User;
import it.unimib.travelhub.util.ServiceLocator;

public class UserViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;

    private MutableLiveData<Result> isUserRegistered;
    private MutableLiveData<Result> isUsernameAlraedyTaken;
    private MutableLiveData<Result> userPreferencesMutableLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
    }

    public MutableLiveData<Result> getUserMutableLiveData(
            String mail, String password, boolean isUserRegistered
    ) {
       if(userMutableLiveData == null){
           getUserData(mail, password, isUserRegistered);
       }

       return userMutableLiveData;
    }

    public MutableLiveData<Result> getUserMutableLiveData
            (String username, String email, String password, boolean isUserRegistered){
        if(userMutableLiveData == null){
           getUserData(username,email,password, isUserRegistered);
        }

        return userMutableLiveData;
    }

    public MutableLiveData<Result> getUserMutableLiveData(){
        if (userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<Result>();
        }
        return userMutableLiveData;
    }

    private void getUserData(String username, String mail, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(username, mail, password, isUserRegistered);
    }

    private void getUserData(String mail, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(mail, password, isUserRegistered);
    }

    private void getUserData(String token) {
        userMutableLiveData = userRepository.getGoogleUser(token);
    }

    public MutableLiveData<Result> getUserPreferencesMutableLiveData() {
        return userPreferencesMutableLiveData;
    }

    public void getUser(String email, String password, boolean isUserRegistered) {
        userRepository.getUser(email, password, isUserRegistered);
    }

    public void getUser(String username, String email, String password, boolean isUserRegistered) {
        userRepository.getUser(username, email, password, isUserRegistered);
    }

    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    public void setAuthenticationError(boolean b) {
        authenticationError = b;
    }

    public MutableLiveData<Result> getGoogleUserMutableLiveData(String idToken) {
        if (userMutableLiveData == null) {
                getUserData(idToken);
            }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> logout(DataEncryptionUtil dataEncryptionUtil, Application application) {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }
        try{
            dataEncryptionUtil.flushEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME);
            ServiceLocator.getInstance().getTravelsRepository(application).deleteAll();
        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "Error while flushing encrypted shared preferences");
        }
        return userMutableLiveData;
    }

    public void updateUserData(String oldUsername, User user) {
        userRepository.updateUserData(oldUsername, user, new UserDataRemoteDataSource.UserCallback() {
            @Override
            public void onUserResponse(Result result) {
                userMutableLiveData.postValue(result);
            }
        });
    }

    public void isUsernameAlreadyTaken(String username) {
        userRepository.isUserRegistered(username, new UserDataRemoteDataSource.UsernameCheckCallback() {
            @Override
            public void onUsernameResponse(Result result) {
                if (result instanceof Result.Error) {
                    isUsernameAlraedyTaken.postValue(new Result.Error("Username: " + username + " not already taken"));
                } else {
                    isUsernameAlraedyTaken.postValue(new Result.UserResponseSuccess(((Result.UserResponseSuccess) result).getData()));
                }
            }
        });
    }

    public void checkUsernames(List<String> usernames){
        List<User> registered = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(usernames.size());

        for(String u : usernames){
            userRepository.isUserRegistered(u, new UserDataRemoteDataSource.UsernameCheckCallback() {
                @Override
                public void onUsernameResponse(Result result) {
                    if(result instanceof Result.Error){
                        isUserRegistered.postValue(new Result.Error("error " + u.toString() + " not registered"));
                    }
                    else{
                        User u = ((Result.UserResponseSuccess) result).getData();
                        registered.add(u);
                        count.set(count.get()-1);
                        if(count.get() == 0){
                            isUserRegistered.postValue(new Result.UsersResponseSuccess(registered));
                            isUserRegistered = null;
                        }

                    }
                }
            });
        }
    }

    public MutableLiveData<Result> getIsUserRegistered() {
        if (isUserRegistered == null) {
            isUserRegistered = new MutableLiveData<Result>();
        }
        return isUserRegistered;
    }

    public MutableLiveData<Result> getIsUsernameAlreadyTaken() {
        if (isUsernameAlraedyTaken == null) {
            isUsernameAlraedyTaken = new MutableLiveData<Result>();
        }
        return isUsernameAlraedyTaken;
    }
}
