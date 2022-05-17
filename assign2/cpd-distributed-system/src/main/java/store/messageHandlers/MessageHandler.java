package store.messageHandlers;

import store.messages.Message;
import store.Store;

import java.util.Arrays;

public abstract class MessageHandler<T extends Message> implements Runnable {
    private final Store store;
    private final T message;

    public MessageHandler(Store store, T message) {
        this.store = store;
        this.message = message;
    }

    public abstract void handle();

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
            handle();
        }
        getStore().getHandledReceivedMessages().push(message);
    }
}
