package store.messageHandlers;

import store.storeRecords.ClusterNodeInformation;
import store.storeRecords.MembershipEvent;
import store.Store;
import store.Utils;
import store.messages.JoinLeaveMessage;
import store.messages.MembershipMessage;
import store.messages.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Stack;

public class JoinLeaveMessageHandler extends MessageHandler<JoinLeaveMessage> {
    public JoinLeaveMessageHandler(Store store, JoinLeaveMessage message) {
        super(store, message, null);
    }

    @Override
    public void handle() {
        System.out.println("Handling JoinLeaveMessage\n\n");

        if (getStore().isStaleNode()) {
            System.out.println("This node has stale information, so won't be responding to JoinLeaveMessage");
            return;
        }

        Stack<Message> receivedMessagesStack = (Stack<Message>) getStore().getHandledReceivedMessages().clone();

        // Ignore repeated JoinLeaveMessage
        while (!receivedMessagesStack.empty()) {
            Message receivedMessage = receivedMessagesStack.pop();

            if (receivedMessage instanceof JoinLeaveMessage joinLeaveMessage) {
                if (joinLeaveMessage.getId().equals(getMessage().getId())) {
                    if ((joinLeaveMessage.isLeave() && !getMessage().isLeave()) || (!joinLeaveMessage.isLeave() && getMessage().isLeave())) {
                        break;
                    }
                    else {
                        System.out.println("Already responded to JoinLeaveMessage");
                        return;
                    }
                }
            }
        }

        if (!getMessage().isLeave()) {
            try (Socket socket = new Socket(InetAddress.getByName(getMessage().getIpAddress()), getMessage().getPort())) {
                MembershipMessage response = new MembershipMessage(getStore().getId(), getStore().getPort(), getStore().getIpAddress(), getStore().getMostRecentMembershipEvents(), getStore().getClusterNodes());
                getStore().sendTCP(response, socket);
                System.out.println("Responded to JoinLeaveMessage");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            System.out.println("Received leave message, not responding with membership");
        }

        registerMembershipEventInStore();
    }

    private void registerMembershipEventInStore() {
        System.out.println("Adding membership event");
        getStore().addMembershipEvent(new MembershipEvent(getMessage().getId(), getMessage().getMembershipCounter()));
        if (getMessage().isLeave()) {
            System.out.println("Removing node from cluster");
            getStore().removeNodeFromClusterRecord(new ClusterNodeInformation(
                    getMessage().getId(), getMessage().getIpAddress(), getMessage().getPort(), Utils.getAngle(getMessage().getId())));
        }
        else {
            System.out.println("Adding node to cluster");
            getStore().addNodeToClusterRecord(new ClusterNodeInformation(
                    getMessage().getId(), getMessage().getIpAddress(),getMessage().getPort(), Utils.getAngle(getMessage().getId())));
        }
    }
}
