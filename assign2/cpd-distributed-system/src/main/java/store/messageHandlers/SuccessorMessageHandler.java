package store.messageHandlers;

import store.Store;
import store.messages.SuccessorMessage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class SuccessorMessageHandler extends MessageHandler<SuccessorMessage> {
    public SuccessorMessageHandler(Store store, SuccessorMessage message) {
        super(store, message, null);
    }

    @Override
    public void handle() throws NoSuchAlgorithmException, IOException {
        for(var keyValue : getMessage().getKeyValues().entrySet()){
            getStore().put(keyValue.getKey(), keyValue.getValue());
        }
    }
}
