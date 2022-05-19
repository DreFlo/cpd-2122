package store.messageHandlers;

import store.messages.Message;
import store.Store;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public abstract class MessageHandler<T extends Message> implements Runnable {
    private final Store store;
    private final T message;
    private final Socket responseSocket;

    public MessageHandler(Store store, T message, Socket responseSocket) {
        this.store = store;
        this.message = message;
        this.responseSocket = responseSocket;
    }

    public abstract void handle() throws NoSuchAlgorithmException, IOException;

    public Store getStore() {
        return store;
    }

    public T getMessage() {
        return message;
    }

    public Socket getResponseSocket() {
        return responseSocket;
    }

    @Override
    public final void run() {
        if (message.getId().equals(store.getId())) {
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
