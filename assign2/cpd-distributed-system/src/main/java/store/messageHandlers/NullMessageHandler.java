package store.messageHandlers;

import store.Store;
import store.messages.NullMessage;

public class NullMessageHandler extends MessageHandler<NullMessage>{

    public NullMessageHandler(Store store, NullMessage message) {
        super(store, message, null);
    }

    @Override
    public void handle() {

    }
}
