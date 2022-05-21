package store.messageHandlers;

import store.Store;
import store.messages.TestLeaveMessage;

public class TestLeaveMessageHandler extends MessageHandler<TestLeaveMessage> {
    public TestLeaveMessageHandler(Store store, TestLeaveMessage message) {
        super(store, message, null);
    }

    @Override
    public void handle() {
        System.out.println("\n\n\n\n\n\nHERE\n\n\n\n\n\n\n");
        System.out.println("Sending leave message");
        getStore().leave();
        getStore().stopListening();
    }
}
