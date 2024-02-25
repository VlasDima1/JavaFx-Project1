package ro.ubbcluj.cs.map.domain.validators;

import ro.ubbcluj.cs.map.domain.Message;

public class MessageValidator implements Validator<Message>{

    @Override
    public void validate(Message entity) throws ValidationException {
        if(entity.getMessage().isEmpty()){
            throw new ValidationException("Your message is empty!");
        }
        if(entity.getFrom() == null){
            throw new ValidationException("Your message is not coming from anyone!");
        }
        if(entity.getTo() == null){
            throw new ValidationException("Your message is not going to anyone!");
        }
    }
}