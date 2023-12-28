package by.ycovich.exception;

public class DeactivatedUserRequestException extends RuntimeException{
    public DeactivatedUserRequestException(String message){
        super(message);
    }
}
