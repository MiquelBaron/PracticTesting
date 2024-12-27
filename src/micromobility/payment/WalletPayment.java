package micromobility.payment;

import exceptions.NotEnoughWalletException;

import java.math.BigDecimal;

public class WalletPayment extends Payment{
    Wallet wallet;
    public WalletPayment(Wallet wallet){
        this.wallet=wallet;
    }
    public void realizeWalletPayment() throws NotEnoughWalletException {
        wallet.deduct(getImpAmount());
    }


}
