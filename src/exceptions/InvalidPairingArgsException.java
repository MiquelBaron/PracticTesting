package exceptions;

public class InvalidPairingArgsException extends Exception {
    //Indicar arg incorrecte
    public InvalidPairingArgsException(String message){
        super(message);
    }
}
