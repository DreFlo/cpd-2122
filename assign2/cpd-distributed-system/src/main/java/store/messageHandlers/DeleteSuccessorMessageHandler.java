package store.messageHandlers;

import store.Store;
import store.messages.DeleteSuccessorMessage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class DeleteSuccessorMessageHandler extends MessageHandler<DeleteSuccessorMessage> {
    public DeleteSuccessorMessageHandler(Store store, DeleteSuccessorMessage message) {
        super(store, message, null);
    }

    @Override
    public void handle() throws NoSuchAlgorithmException, IOException {
        getStore().delete(getMessage().getKey());
    }
}
