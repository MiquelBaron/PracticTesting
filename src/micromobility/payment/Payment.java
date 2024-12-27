package micromobility.payment;

import data.UserAccount;
import micromobility.JourneyService;

import java.math.BigDecimal;

public class Payment {
    private BigDecimal impAmount;
    private JourneyService journeyService;

    public void setImpAmount(BigDecimal impAmount) {
        this.impAmount = impAmount;
    }

    public BigDecimal getImpAmount() {
        return impAmount;
    }

    public void setJourneyService(JourneyService journeyService) {
        this.journeyService = journeyService;
    }
}
