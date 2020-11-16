package net.ali.modbot.exceptions;

public class InvalidCommandException extends RuntimeException{

    public InvalidCommandException(String message){
        super(message);
    }
}
