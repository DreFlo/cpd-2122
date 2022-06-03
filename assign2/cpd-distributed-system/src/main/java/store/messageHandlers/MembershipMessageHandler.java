package store.messageHandlers;

import store.Store;
import store.messages.MembershipMessage;
import store.storeRecords.ClusterNodeInformation;
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
                try {
                    ClusterNodeInformation clusterNodeInformation = getClusterNodeInformationFromSortedSetById(membershipMessage.getClusterNodes(), receivedMembershipEvent.nodeId());
                    if (receivedMembershipEvent.isLeave()) {
                        store.removeNodeFromClusterRecord(clusterNodeInformation);
                    } else {
                        store.addNodeToClusterRecord(clusterNodeInformation);
                    }
                }
                catch (RuntimeException e) {
                    System.out.println("Could not find node: " + receivedMembershipEvent.nodeId() + " in set");
                }
            }
        }
    }
}
