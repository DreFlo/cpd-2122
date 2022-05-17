package store.messageHandlers;

import store.ClusterNodeInformation;
import store.MembershipEvent;
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

        boolean skip = false;

        while (!receivedMessagesStack.empty()) {
            Message receivedMessage = receivedMessagesStack.pop();

            if (receivedMessage instanceof JoinLeaveMessage joinLeaveMessage) {
                if (Utils.keyToString(joinLeaveMessage.getId()).equals(Utils.keyToString(getMessage().getId()))) {
                    if ((joinLeaveMessage.isLeave() && !getMessage().isLeave()) || (!joinLeaveMessage.isLeave() && getMessage().isLeave())) {
                        registerMembershipEventInStore();
                        skip = true;
                        break;
                    }
                    else {
                        System.out.println("Already responded to JoinLeaveMessage");
                        return;
                    }
                }
            }
        }

        if (!skip) {
            registerMembershipEventInStore();
        }

        System.out.println("Arrived");

        try (Socket socket = new Socket("127.0.0.1", getMessage().getPort())){
            MembershipMessage response = new MembershipMessage(getStore().getId(), getStore().getPort(), getStore().getMostRecentMembershipEvents(), getStore().getClusterNodes());
            getStore().sendTCP(response, socket);
            System.out.println("Responded to JoinLeaveMessage");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerMembershipEventInStore() {
        System.out.println("Adding membership event");
        getStore().addMembershipEvent(new MembershipEvent(getMessage().getId(), getMessage().getMembershipCounter()));
        if (getMessage().isLeave()) {
            System.out.println("Removing node from cluster");
            getStore().removeNodeFromClusterRecord(new ClusterNodeInformation(getMessage().getId(), getMessage().getPort()));
        }
        else {
            System.out.println("Adding node to cluster");
            getStore().addNodeToClusterRecord(new ClusterNodeInformation(getMessage().getId(), getMessage().getPort()));
        }
    }
}
