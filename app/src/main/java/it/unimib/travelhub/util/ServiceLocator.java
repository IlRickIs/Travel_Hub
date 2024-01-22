package it.unimib.travelhub.util;

import android.content.Context;

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
}
