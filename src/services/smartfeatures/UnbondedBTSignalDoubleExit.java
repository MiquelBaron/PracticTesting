package services.smartfeatures;

import java.net.ConnectException;

public class UnbondedBTSignalDoubleExit implements UnbondedBTSignal{
    private boolean connected;
    public UnbondedBTSignalDoubleExit(){
        connected=false;
    }
    @Override
    public void BTbroadcast() throws ConnectException {
        connected=true;
    }
    }

