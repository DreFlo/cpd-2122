package store.messageHandlers;

import store.Store;
import store.messages.TestJoinMessage;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class TestJoinMessageHandler extends MessageHandler<TestJoinMessage>{
    public TestJoinMessageHandler(Store store, TestJoinMessage message) {
        super(store, message, null);
    }

    @Override
    public void handle() throws NoSuchAlgorithmException, IOException {
        getStore().join();
        System.out.println("LEFT JOIN");
        getStore().listen();
    }
}
