package it.unimib.travelhub.data.repository.user;

import java.util.List;

import it.unimib.travelhub.model.User;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
    void onSuccessFromGettingUserPreferences();
    void onFailureFromRemoteDatabase(String message);
    void onSuccessLogout();
}
