package services.smartfeatures;

import exceptions.PMVPhisicalException;
import exceptions.ProceduralException;

import java.net.ConnectException;

public class ArduinoMicroControllerDoubleExit implements ArduinoMicroController{
    @Override
    public void setBTconnection() throws ConnectException {

    }

    @Override
    public void startDriving() throws PMVPhisicalException, ConnectException, ProceduralException {

    }

    @Override
    public void stopDriving() throws PMVPhisicalException, ConnectException, ProceduralException {

    }

    @Override
    public void undoBTconnection() {

    }
}
