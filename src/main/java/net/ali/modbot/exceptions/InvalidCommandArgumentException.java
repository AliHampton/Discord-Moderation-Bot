package net.ali.modbot.exceptions;

public class InvalidCommandArgumentException extends RuntimeException{

    public InvalidCommandArgumentException(String message){
        super(message);
    }
}
