package micromobility.payment;

import data.ServiceID;
import data.UserAccount;
import micromobility.JourneyService;

import java.math.BigDecimal;

public class Payment {
    private BigDecimal impAmount;
    private ServiceID serviceID;

    public void setImpAmount(BigDecimal impAmount) {
        this.impAmount = impAmount;
    }

    public BigDecimal getImpAmount() {
        return impAmount;
    }

    public void setServiceID(ServiceID serviceID) {
        this.serviceID = serviceID;
    }

    public ServiceID getServiceID() {
        return serviceID;
    }
}
