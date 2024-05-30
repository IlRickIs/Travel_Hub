package it.unimib.travelhub.model;

/**
 * Class that represents the result of an action that requires
 * the use of a Web Service or a local database.
 */
public abstract class Result {
    private Result() {}

    public boolean isSuccess() {
        return this instanceof UserResponseSuccess;
    }

    public static final class TravelsResponseSuccess extends Result {
        private final TravelsResponse travelsResponse;
        public TravelsResponseSuccess(TravelsResponse travelsResponse) {
            this.travelsResponse = travelsResponse;
        }
        public TravelsResponse getData() {
            return travelsResponse;
        }
    }

    public static final class UserResponseSuccess extends Result {
        private final User user;
        public UserResponseSuccess(User user) {
            this.user = user;
        }
        public User getData() {
            return user;
        }
    }

    /**
     * Class that represents an error occurred during the interaction
     * with a Web Service or a local database.
     */
    public static final class Error extends Result {
        private final String message;
        public Error(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }
}
