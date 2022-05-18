package store.messageHandlers;

import store.messages.Message;
import store.Store;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public abstract class MessageHandler<T extends Message> implements Runnable {
    private final Store store;
    private final T message;

    public MessageHandler(Store store, T message) {
        this.store = store;
        this.message = message;
    }

    public abstract void handle() throws NoSuchAlgorithmException, IOException;

    public Store getStore() {
        return store;
    }

    public T getMessage() {
        return message;
    }

    @Override
    public final void run() {
        if (Arrays.equals(message.getId(), store.getId())) {
            System.out.println("Ignoring message sent by self");
        }
        else {
            try {
                handle();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        getStore().getHandledReceivedMessages().push(message);
    }
}
