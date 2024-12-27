package Tests;

import Tests.Interfaces.BroadcastStationIDInterface;
import data.*;
import micromobility.JourneyRealizeHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.smartfeatures.*;
import java.io.IOException;
import java.net.ConnectException;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBroadcastStationIDExit implements BroadcastStationIDInterface {
    JourneyRealizeHandler journeyRealizeHandler;
    StationID st;

    @BeforeEach
    public void setUp(){
        UnbondedBTSignal unbondedBTSignal=new UnbondedBTSignalDoubleExit();
        st=new StationID("1", new GeographicPoint(10,10));
        this.journeyRealizeHandler= new JourneyRealizeHandler();
        journeyRealizeHandler.setUnbondedBTSignal(unbondedBTSignal);

    }

    @Override
    @Test
    public void testBroadcastStationID() throws ConnectException {
        journeyRealizeHandler.broadcastStationID(st);
        assertEquals(journeyRealizeHandler.getStationID(),st);
    }
}
