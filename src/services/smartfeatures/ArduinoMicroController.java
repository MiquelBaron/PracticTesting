package services.smartfeatures;

import exceptions.*;

import java.net.ConnectException;

public interface ArduinoMicroController { // Software for microcontrollers
    public void setBTconnection () throws ConnectException;
    public void startDriving () throws PMVPhisicalException, ConnectException,
            ProceduralException;
    public void stopDriving () throws PMVPhisicalException, ConnectException,
            ProceduralException;
    public void undoBTconnection ();
}
