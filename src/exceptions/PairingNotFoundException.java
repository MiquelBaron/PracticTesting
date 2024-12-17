package exceptions;

public class PairingNotFoundException extends Exception{
    //No se dispone de informacion asociada a dicho emparejamiento en el server
    public PairingNotFoundException(String message){
        super(message);
    }
}
