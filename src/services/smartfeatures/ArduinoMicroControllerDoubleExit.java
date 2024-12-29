package services.smartfeatures;

import exceptions.PMVPhisicalException;
import exceptions.ProceduralException;

import java.net.ConnectException;

public class ArduinoMicroControllerDoubleExit implements ArduinoMicroController{
    private boolean connected;
    private boolean driving;
    public ArduinoMicroControllerDoubleExit(){
        this.connected=false;
        this.driving=false;
    }

    //Simulating connection and driving
    @Override
    public void setBTconnection() throws ConnectException {
        connected=true;
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
        connected=false;
    }
}
