package services.smartfeatures;

import java.net.ConnectException;

public class UnbondedBTSignalDoubleFail implements UnbondedBTSignal{
    @Override
    public void BTbroadcast() throws ConnectException {
        throw new ConnectException("Connection error");
    }
}
