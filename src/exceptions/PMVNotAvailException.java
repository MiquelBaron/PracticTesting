package exceptions;

public class PMVNotAvailException extends Exception {
    //Indicar que el vehicle esta vinculat a un altre user
    public PMVNotAvailException(String message){
        super(message);
    }
}
