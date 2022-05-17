package store.messageHandlers;

import jdk.jshell.spi.ExecutionControl;
import store.Store;
import store.messages.JoinLeaveMessage;
import store.messages.MembershipMessage;
import store.messages.Message;
import store.messages.NullMessage;

public class MessageHandlerBuilder {
    public static MessageHandler<? extends Message> get(Store store, Message message) throws ExecutionControl.NotImplementedException {
        if (message instanceof JoinLeaveMessage joinLeaveMessage) {
            return new JoinLeaveMessageHandler(store, joinLeaveMessage);
        }
        else if (message instanceof MembershipMessage membershipMessage) {
            return new MembershipMessageHandler(store, membershipMessage);
        } else if (message instanceof NullMessage nullMessage) {
            return new NullMessageHandler(store, nullMessage);
        }
        else {
            throw new ExecutionControl.NotImplementedException("Not implemented for message type: " + message.getClass().getName());
        }
    }
}
