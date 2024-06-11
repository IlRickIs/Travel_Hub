package it.unimib.travelhub.util;

import android.app.Application;
import android.content.Context;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.travelhub.crypto_util.DataEncryptionUtil;
import it.unimib.travelhub.data.database.TravelsRoomDatabase;
import it.unimib.travelhub.data.repository.travels.ITravelsRepository;
import it.unimib.travelhub.data.repository.travels.TravelsRepository;
import it.unimib.travelhub.data.repository.user.IUserRepository;
import it.unimib.travelhub.data.repository.user.UserRepository;
import it.unimib.travelhub.data.source.BaseTravelsLocalDataSource;
import it.unimib.travelhub.data.source.BaseTravelsRemoteDataSource;
import it.unimib.travelhub.data.source.TravelsLocalDataSource;
import it.unimib.travelhub.data.source.TravelsMockRemoteDataSource;
import it.unimib.travelhub.data.source.TravelsRemoteDataSource;
import it.unimib.travelhub.data.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.travelhub.data.user.BaseUserDataRemoteDataSource;
import it.unimib.travelhub.data.user.UserAuthenticationRemoteDataSource;
import it.unimib.travelhub.data.user.UserDataRemoteDataSource;
import it.unimib.travelhub.data.user.UserRemoteFirestoreDataSource;
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
                new UserRemoteFirestoreDataSource(sharedPreferencesUtil);


        return new UserRepository(userRemoteAuthenticationDataSource,
                userDataRemoteDataSource);
    }

    public ITravelsRepository getTravelsRepository(Application application) {
        BaseTravelsRemoteDataSource travelsRemoteDataSource;
        BaseTravelsLocalDataSource travelsLocalDataSource;
        DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(application);
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);

        JSONParserUtil jsonParserUtil = new JSONParserUtil(application);
//        travelsRemoteDataSource = new TravelsMockRemoteDataSource(jsonParserUtil);
        travelsRemoteDataSource = new TravelsRemoteDataSource(dataEncryptionUtil);

        travelsLocalDataSource = new TravelsLocalDataSource(getTravelsDao(application));

        return new TravelsRepository(travelsLocalDataSource, travelsRemoteDataSource, sharedPreferencesUtil);

    }

    public TravelsRoomDatabase getTravelsDao(Application application) {
        return TravelsRoomDatabase.getDatabase(application);
    }
}

