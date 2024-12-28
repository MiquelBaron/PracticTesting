package micromobility.payment;

import exceptions.NotEnoughWalletException;

import java.math.BigDecimal;

public class Wallet {
    private BigDecimal balance;
    public Wallet(BigDecimal balance){
        if(balance.compareTo(BigDecimal.ZERO)<0){
            throw new IllegalArgumentException();
        }
        this.balance=balance;
    }
    public void deduct (BigDecimal imp) throws NotEnoughWalletException {
        if(imp.compareTo(balance)>0){
            throw new NotEnoughWalletException("Not enough balance");
        }
        balance = balance.subtract(imp);
    }

    public BigDecimal getBalance() {
        return balance;
    }
}

