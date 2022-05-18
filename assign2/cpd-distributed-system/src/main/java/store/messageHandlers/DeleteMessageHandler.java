package store.messageHandlers;

import store.Store;
import store.messages.DeleteMessage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class DeleteMessageHandler extends MessageHandler<DeleteMessage> {
    public DeleteMessageHandler(Store store, DeleteMessage message) {
        super(store, message);
    }

    @Override
    public void handle() throws NoSuchAlgorithmException, IOException {
        getStore().delete(getMessage().getKey());
    }
}
