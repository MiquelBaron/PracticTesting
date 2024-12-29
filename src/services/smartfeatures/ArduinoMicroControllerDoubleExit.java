package services.smartfeatures;

import exceptions.PMVPhisicalException;
import exceptions.ProceduralException;

import java.net.ConnectException;

public class ArduinoMicroControllerDoubleExit implements ArduinoMicroController{
    private boolean connection;
    private boolean driving;
    public ArduinoMicroControllerDoubleExit(){
        connection=false;
        driving=false;
    }
    @Override
    public void setBTconnection() throws ConnectException {
        connection=true;
    }

    @Override
    public void startDriving() throws PMVPhisicalException, ConnectException, ProceduralException {
        driving=true;
    }

    @Override
    public void stopDriving() throws PMVPhisicalException, ConnectException, ProceduralException {
        driving=false;
    }

    @Override
    public void undoBTconnection() {
        connection=false;
    }
}
