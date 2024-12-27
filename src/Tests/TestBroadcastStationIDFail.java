
package Tests;


import Tests.Interfaces.BroadcastStationIDInterface;
import data.*;
import micromobility.JourneyRealizeHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.smartfeatures.*;
import java.net.ConnectException;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class TestBroadcastStationIDFail implements BroadcastStationIDInterface {

    JourneyRealizeHandler journeyRealizeHandler;
    StationID st;

    @BeforeEach
    public void setUp(){
        UnbondedBTSignal unbondedBTSignal=new UnbondedBTSignalDoubleFail();
        st=new StationID("1", new GeographicPoint(10,10));
        this.journeyRealizeHandler= new JourneyRealizeHandler();
        journeyRealizeHandler.setUnbondedBTSignal(unbondedBTSignal);

    }

    @Override
    @Test
    public void testBroadcastStationID() {
        assertThrows(ConnectException.class,
                ()-> journeyRealizeHandler.broadcastStationID(st));
    }

}
