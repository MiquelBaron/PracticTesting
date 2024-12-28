package services;
import data.*;
import exceptions.InvalidPairingArgsException;
import exceptions.InvalidPaymentArgsException;
import exceptions.PMVNotAvailException;
import exceptions.PairingNotFoundException;
import micromobility.JourneyService;

import java.math.BigDecimal;
import java.net.*;
import java.time.*;

/**
 * External services involved in the shared micromobility system
 */
public interface Server {
    void checkPMVAvail(VehicleID vhID)
            throws PMVNotAvailException, ConnectException;
    void registerPairing(UserAccount user, VehicleID veh, StationID st,
                         GeographicPoint loc, LocalDateTime date)
            throws InvalidPairingArgsException, ConnectException;
    void stopPairing(UserAccount user, VehicleID veh, StationID st,
                     GeographicPoint loc, LocalDateTime date, float avSp, float dist,
                     int dur, BigDecimal imp)
            throws InvalidPairingArgsException, ConnectException, PairingNotFoundException;
    // Internal operations
    void setPairing(UserAccount user, VehicleID veh, StationID st,
                    GeographicPoint loc, LocalDateTime
                            date);
    void unPairRegisterService(JourneyService s) throws PairingNotFoundException;
    void registerLocation(VehicleID veh, StationID st);
    void registerPayment(ServiceID serviceID, UserAccount userAccount, BigDecimal imp, char payMeth) throws InvalidPaymentArgsException;
}