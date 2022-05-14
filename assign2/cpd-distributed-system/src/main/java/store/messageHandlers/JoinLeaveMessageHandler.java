package store.messageHandlers;

import store.Store;
import store.Utils;
import store.messages.JoinLeaveMessage;
import store.messages.MembershipMessage;
import store.messages.Message;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Stack;

public class JoinLeaveMessageHandler extends MessageHandler<JoinLeaveMessage> {
    public JoinLeaveMessageHandler(Store store, JoinLeaveMessage message) {
        super(store, message);
    }

    @Override
    public void handle() {
        System.out.println("Handling JoinLeaveMessage\n\n");
        if (Arrays.equals(getMessage().getId(), getStore().getId())) return;

        Stack<Message> receivedMessagesStack = (Stack<Message>) getStore().getHandledReceivedMessages().clone();

        System.out.println("\n\n\n" + receivedMessagesStack + "\n\n\n");

        while (!receivedMessagesStack.empty()) {
            Message receivedMessage = receivedMessagesStack.pop();

            if (receivedMessage instanceof JoinLeaveMessage) {
                if (Arrays.equals(receivedMessage.getId(),getMessage().getId())) {
                    if (((JoinLeaveMessage) receivedMessage).isLeave() && !getMessage().isLeave()) {
                        System.out.println("Entering");
                        break;
                    }
                    else if (!((JoinLeaveMessage) receivedMessage).isLeave() && getMessage().isLeave()) {
                        System.out.println("Leaving");
                        break;
                    }
                    else {
                        System.out.println("Already responded to JoinLeaveMessage");
                        return;
                    }
                }
            }
        }

        try (Socket socket = new Socket("127.0.0.1", getMessage().getPort())){
            MembershipMessage response = (new MembershipMessage(getStore().getId()));
            getStore().sendTCP(response, socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
