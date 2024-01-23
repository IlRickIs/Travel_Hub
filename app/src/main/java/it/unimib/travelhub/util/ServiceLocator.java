package it.unimib.travelhub.util;

import android.app.Application;
import android.content.Context;

import it.unimib.travelhub.data.repository.user.IUserRepository;
import it.unimib.travelhub.data.repository.user.UserRepository;
import it.unimib.travelhub.data.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.travelhub.data.user.BaseUserDataRemoteDataSource;
import it.unimib.travelhub.data.user.UserAuthenticationRemoteDataSource;
import it.unimib.travelhub.data.user.UserDataRemoteDataSource;
import it.unimib.travelhub.model.IValidator;

public class ServiceLocator {
    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {}

    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    public IValidator getCredentialsValidator(Context context){
       CredentialValidator validator =  CredentialValidator.getInstance();
       validator.setContext(context);
       return validator;
    }


    public IUserRepository getUserRepository(Application application) {
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);

        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource =
                new UserAuthenticationRemoteDataSource();

        BaseUserDataRemoteDataSource userDataRemoteDataSource =
                new UserDataRemoteDataSource(sharedPreferencesUtil);


        return new UserRepository(userRemoteAuthenticationDataSource,
                userDataRemoteDataSource);
    }
}

