package services.smartfeatures;

import java.net.ConnectException;

public class UnbondedBTSignalDoubleExit implements UnbondedBTSignal{
    private boolean broadcast;
    public UnbondedBTSignalDoubleExit(){
        broadcast=false;
    }
    @Override
    public void BTbroadcast() throws ConnectException {
        broadcast=true;
    }
}
