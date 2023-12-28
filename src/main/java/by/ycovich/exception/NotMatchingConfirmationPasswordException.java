package by.ycovich.exception;

public class NotMatchingConfirmationPasswordException extends RuntimeException{
    public NotMatchingConfirmationPasswordException(String message){
        super(message);
    }
}
