package store.messageHandlers;

import jdk.jshell.spi.ExecutionControl;
import store.Store;
import store.messages.*;

import java.net.Socket;

public class MessageHandlerBuilder {
    public static MessageHandler<? extends Message> get(Store store, Message message, Socket responseSocket) throws ExecutionControl.NotImplementedException {
        if (message instanceof JoinLeaveMessage joinLeaveMessage) {
            return new JoinLeaveMessageHandler(store, joinLeaveMessage);
        }
        else if (message instanceof MembershipMessage membershipMessage) {
            return new MembershipMessageHandler(store, membershipMessage);
        } else if (message instanceof NullMessage nullMessage) {
            return new NullMessageHandler(store, nullMessage);
        } else if (message instanceof PutMessage putMessage) {
            return new PutMessageHandler(store, putMessage, responseSocket);
        } else if (message instanceof DeleteMessage deleteMessage) {
            return new DeleteMessageHandler(store, deleteMessage, responseSocket);
        } else if (message instanceof GetMessage getMessage) {
            if (responseSocket == null) {
                throw new IllegalArgumentException("responseSocket is null");
            }
            return new GetMessageHandler(store, getMessage, responseSocket);
        } else if (message instanceof LeaveKeyTransferMessage leaveKeyTransferMessage) {
            return new LeaveKeyTransferMessageHandler(store, leaveKeyTransferMessage);
        } else if (message instanceof JoinKeyTransferMessage joinKeyTransferMessage) {
            if (responseSocket == null) {
                throw new IllegalArgumentException("responseSocket is null");
            }
            return new JoinKeyTransferMessageHandler(store, joinKeyTransferMessage, responseSocket);
        } else if (message instanceof TestLeaveMessage testLeaveMessage) {
            return new TestLeaveMessageHandler(store, testLeaveMessage);
        } else if (message instanceof TestJoinMessage testJoinMessage) {
            return new TestJoinMessageHandler(store, testJoinMessage);
        } else if (message instanceof PutSuccessorMessage putSuccessorMessage) {
            return new PutSuccessorMessageHandler(store, putSuccessorMessage);
        } else if (message instanceof DeleteSuccessorMessage deleteSuccessorMessage) {
            return new DeleteSuccessorMessageHandler(store, deleteSuccessorMessage);
        } else if (message instanceof CheckReplicationMessage checkReplicationMessage) {
            return new CheckReplicationMessageHandler(store, checkReplicationMessage);
        } else {
            throw new ExecutionControl.NotImplementedException("Not implemented for message type: " + message.getClass().getName());
        }
    }
}
