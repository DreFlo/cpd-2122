package store.messageHandlers;

import store.Store;
import store.messages.JoinLeaveMessage;
import store.messages.MembershipMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class JoinLeaveMessageHandler extends MessageHandler<JoinLeaveMessage> {
    public JoinLeaveMessageHandler(Store store, JoinLeaveMessage message) {
        super(store, message);
    }

    @Override
    public void run() {
        if (Arrays.equals(getMessage().getId(), getStore().getId())) return;
        try (Socket socket = new Socket("127.0.0.1", getMessage().getPort())){
            MembershipMessage response = (new MembershipMessage(getStore().getId()));
            getStore().sendTCP(response, socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
