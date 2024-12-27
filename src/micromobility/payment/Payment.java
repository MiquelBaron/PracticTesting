package micromobility.payment;

import java.math.BigDecimal;

public class Payment {
    private BigDecimal impAmount;

    public void setImpAmount(BigDecimal impAmount) {
        this.impAmount = impAmount;
    }

    public BigDecimal getImpAmount() {
        return impAmount;
    }
}
