package it.unimib.travelhub.util;

import android.app.Application;
import android.content.Context;

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
        return new CredentialValidator(context);
    }
}
