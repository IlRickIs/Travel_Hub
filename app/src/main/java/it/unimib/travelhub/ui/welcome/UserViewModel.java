package it.unimib.travelhub.ui.welcome;

import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.travelhub.crypto_util.DataEncryptionUtil;
import it.unimib.travelhub.data.repository.user.IUserRepository;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.User;

public class UserViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
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

    public MutableLiveData<Result> logout(DataEncryptionUtil dataEncryptionUtil) {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }

        try{
            dataEncryptionUtil.flushEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME);
        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "Error while flushing encrypted shared preferences");
        }

        return userMutableLiveData;
    }

}
