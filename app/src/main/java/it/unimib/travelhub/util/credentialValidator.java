package it.unimib.travelhub.util;

import org.apache.commons.validator.routines.EmailValidator;

public class credentialValidator implements IValidator{


    @Override
    public boolean validateMail(String email) {
        return EmailValidator.getInstance().isValid((email));
    }

    @Override
    public boolean validatePassword() {
        return false;
    }
}
