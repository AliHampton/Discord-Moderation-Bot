package net.ali.modbot.exceptions;

public class InvalidCommandStateException extends RuntimeException{

    public InvalidCommandStateException(String message){
        super(message);
    }
}
