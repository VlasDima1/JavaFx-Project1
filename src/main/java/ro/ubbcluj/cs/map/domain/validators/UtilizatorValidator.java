package ro.ubbcluj.cs.map.domain.validators;

import ro.ubbcluj.cs.map.domain.Utilizator;
import ro.ubbcluj.cs.map.domain.validators.ValidationException;
import ro.ubbcluj.cs.map.domain.validators.Validator;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        //String first=entity.getFirstName();
        for (char c: entity.getFirstName().toCharArray())
        {
            if (Character.isDigit(c)){
                throw new ValidationException("Primul nume nu este valid");
            }
        }
        for (char c: entity.getLastName().toCharArray()) {
            if (Character.isDigit(c)) {
                throw new ValidationException("Al doilea nume nu este valid");
            }
        }
        if (entity.getFirstName().length() <2)
        {
            throw new ValidationException("Primul nume este prea mic");
        }
        if (entity.getLastName().length() <2)
        {
            throw new ValidationException("Al doilea nume este prea mic");
        }
        if (entity.getUsername().length() <3 || Character.isDigit(entity.getUsername().charAt(0)))
        {
            throw new ValidationException("username invalid");
        }
        if (entity.getPassword().length() <2)
        {
            throw new ValidationException("Parola prea mica");
        }
    }
}

