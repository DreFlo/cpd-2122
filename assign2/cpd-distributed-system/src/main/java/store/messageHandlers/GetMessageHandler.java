package store.messageHandlers;

import store.Store;
import store.messages.GetMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class GetMessageHandler extends MessageHandler<GetMessage> {
    public GetMessageHandler(Store store, GetMessage message, Socket responseSocket) {
        super(store, message, responseSocket);
    }

    @Override
    public void handle() throws IOException {
        byte[] value = getStore().get(getMessage().getKey());

        getResponseSocket().getOutputStream().write(value);
        getResponseSocket().close();
    }
}
