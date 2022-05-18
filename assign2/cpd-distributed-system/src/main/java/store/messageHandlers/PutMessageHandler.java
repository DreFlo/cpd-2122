package store.messageHandlers;

import store.Store;
import store.Utils;
import store.messages.PutMessage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class PutMessageHandler extends MessageHandler<PutMessage> {
    public PutMessageHandler(Store store, PutMessage message) {
        super(store, message);
    }

    @Override
    public void handle() throws NoSuchAlgorithmException, IOException {
        getStore().put(getMessage().getKey(), getMessage().getValue());
    }
}
