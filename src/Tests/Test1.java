package Tests;
import data.*;
import exceptions.*;
import micromobility.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.*;
import services.smartfeatures.QRDecoder;
import services.smartfeatures.QRDecoderDoubleExit;

import java.net.ConnectException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Test1 {
    JourneyRealizeHandler journeyRealizeHandler;
    Server serverExit;
    Server serverFail;
    QRDecoder qrDecoderExit;
    @BeforeEach
    void setUp(){
         journeyRealizeHandler = new JourneyRealizeHandler();
         serverExit = new ServerDoubleExit();
         serverFail = new ServerDoubleFail();
         qrDecoderExit= new QRDecoderDoubleExit();


    }

    @Test
    public void test1(){
        journeyRealizeHandler.setServer(serverFail);
        assertThrows(ConnectException.class, () -> journeyRealizeHandler.scanQR());
    }

}
