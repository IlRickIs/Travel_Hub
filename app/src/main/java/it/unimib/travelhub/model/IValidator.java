package it.unimib.travelhub.model;

public interface IValidator {
    public ValidationResult validateMail(String mail);
    public ValidationResult validatePassword(String password);
}
