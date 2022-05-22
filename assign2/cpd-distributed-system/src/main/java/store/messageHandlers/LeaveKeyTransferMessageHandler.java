package store.messageHandlers;

import store.Store;
import store.messages.LeaveKeyTransferMessage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class LeaveKeyTransferMessageHandler extends MessageHandler<LeaveKeyTransferMessage> {
    public LeaveKeyTransferMessageHandler(Store store, LeaveKeyTransferMessage message) {
        super(store, message, null);
    }

    @Override
    public void handle() throws NoSuchAlgorithmException, IOException {
        for(var keyValue : getMessage().getKeyValues().entrySet()){
            getStore().put(keyValue.getKey(), keyValue.getValue());
        }
    }
}
