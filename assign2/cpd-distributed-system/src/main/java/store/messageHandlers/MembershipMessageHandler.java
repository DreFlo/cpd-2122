package store.messageHandlers;

import store.Store;
import store.Utils;
import store.messages.MembershipMessage;
import store.storeRecords.MembershipEvent;

import static store.Utils.getClusterNodeInformationFromSortedSetById;

public class MembershipMessageHandler extends MessageHandler<MembershipMessage> {
    public MembershipMessageHandler(Store store, MembershipMessage message) {
        super(store, message, null);
    }

    @Override
    public void handle() {
        MembershipMessage membershipMessage = getMessage();
        Store store = getStore();

        for (MembershipEvent receivedMembershipEvent : membershipMessage.getMembershipLog()) {
            if (store.addMembershipEvent(receivedMembershipEvent)) {
                if (receivedMembershipEvent.isLeave()) {
                    store.removeNodeFromClusterRecord(getClusterNodeInformationFromSortedSetById(membershipMessage.getClusterNodes(), receivedMembershipEvent.nodeId()));
                } else {
                    store.addNodeToClusterRecord(getClusterNodeInformationFromSortedSetById(membershipMessage.getClusterNodes(), receivedMembershipEvent.nodeId()));
                }
            }
        }
    }
}
