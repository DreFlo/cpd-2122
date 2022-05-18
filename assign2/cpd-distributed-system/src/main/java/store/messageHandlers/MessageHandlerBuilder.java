package store.messageHandlers;

import jdk.jshell.spi.ExecutionControl;
import store.Store;
import store.messages.*;

public class MessageHandlerBuilder {
    public static MessageHandler<? extends Message> get(Store store, Message message) throws ExecutionControl.NotImplementedException {
        if (message instanceof JoinLeaveMessage joinLeaveMessage) {
            return new JoinLeaveMessageHandler(store, joinLeaveMessage);
        }
        else if (message instanceof MembershipMessage membershipMessage) {
            return new MembershipMessageHandler(store, membershipMessage);
        } else if (message instanceof NullMessage nullMessage) {
            return new NullMessageHandler(store, nullMessage);
        } else if (message instanceof PutMessage putMessage) {
            return new PutMessageHandler(store, putMessage);
        } else if (message instanceof DeleteMessage deleteMessage) {
            return new DeleteMessageHandler(store, deleteMessage);
        } else if (message instanceof GetMessage getMessage) {
            return new GetMessageHandler(store, getMessage);
        } else {
            throw new ExecutionControl.NotImplementedException("Not implemented for message type: " + message.getClass().getName());
        }
    }
}
