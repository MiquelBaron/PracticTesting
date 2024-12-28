package services.smartfeatures;

import exceptions.PMVPhisicalException;
import exceptions.ProceduralException;

import java.net.ConnectException;

public class ArduinoMicroControllerDoubleFail implements ArduinoMicroController{

    private boolean connectError;
    private boolean phsicalError;

    public ArduinoMicroControllerDoubleFail(boolean connectError, boolean phsicalError){
        this.connectError=connectError;
        this.phsicalError=phsicalError;
    }
    @Override
    public void setBTconnection() throws ConnectException {
        if(connectError) {
            throw new ConnectException("Connection error");
        }
    }

    @Override
    public void startDriving() throws PMVPhisicalException, ConnectException, ProceduralException {
        if(connectError){throw new ConnectException("Connect exception");}
        if(phsicalError){throw new PMVPhisicalException("Phisical exception");}
        throw new ProceduralException("Procedural exception");
    }

    @Override
    public void stopDriving() throws PMVPhisicalException, ConnectException, ProceduralException {
        if(connectError){throw new ConnectException("Connect exception");}
        if(phsicalError){throw new PMVPhisicalException("Phisical exception");}
        throw new ProceduralException("Procedural exception");
    }

    @Override
    public void undoBTconnection() {

    }
}
