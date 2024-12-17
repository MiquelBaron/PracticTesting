package services.smartfeatures;

import exceptions.PMVPhisicalException;
import exceptions.ProceduralException;

import java.net.ConnectException;

public class ArduinoMicroControllerDoubleFail implements ArduinoMicroController{
    @Override
    public void setBTconnection() throws ConnectException {
        throw new ConnectException("Connection error");
    }

    @Override
    public void startDriving() throws PMVPhisicalException, ConnectException, ProceduralException {
        throw new PMVPhisicalException("Phisical exception");
    }

    @Override
    public void stopDriving() throws PMVPhisicalException, ConnectException, ProceduralException {
        throw new PMVPhisicalException("Phisical exception");
    }

    @Override
    public void undoBTconnection() {

    }
}
