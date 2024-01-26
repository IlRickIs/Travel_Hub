package it.unimib.travelhub.data.user;

import java.util.Set;

import it.unimib.travelhub.data.repository.user.UserResponseCallback;
import it.unimib.travelhub.model.User;

/**
 * Base class to get the user data from a remote source.
 */
public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);
    public abstract void getUserFavorite(String idToken);
    public abstract void getUserPreferences(String idToken);
    public abstract void saveUserPreferences(String preferences);

    public abstract void isUsernameAvailable(String username);

    public abstract void isUsernameTaken(String username, String email, String password);

}
