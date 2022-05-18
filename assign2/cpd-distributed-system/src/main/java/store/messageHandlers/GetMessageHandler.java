package store.messageHandlers;

import store.Store;
import store.messages.GetMessage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class GetMessageHandler extends MessageHandler<GetMessage> {
    public GetMessageHandler(Store store, GetMessage message) {
        super(store, message);
    }

    @Override
    public void handle() {
        String value = getStore().get(getMessage().getKey());
        getMessage().setValue(value);
    }
}
