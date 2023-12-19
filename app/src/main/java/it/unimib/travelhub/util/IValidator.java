package it.unimib.travelhub.util;

public interface IValidator {
    public ValidationResult validateMail(String mail);
    public ValidationResult validatePassword(String password);
}
