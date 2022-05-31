package store.messageHandlers;

import store.Store;
import store.messages.PutSuccessorMessage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class PutSuccessorMessageHandler extends MessageHandler<PutSuccessorMessage> {
    public PutSuccessorMessageHandler(Store store, PutSuccessorMessage message) {
        super(store, message, null);
    }

    @Override
    public void handle() throws NoSuchAlgorithmException, IOException {
        for(var keyValue : getMessage().getKeyValues().entrySet()){
            getStore().put(keyValue.getKey(), keyValue.getValue());
        }
    }
}
