package store.messageHandlers;

import store.Store;
import store.messages.MembershipMessage;
import store.storeRecords.ClusterNodeInformation;
import store.storeRecords.MembershipEvent;

import java.util.Objects;
import java.util.SortedSet;

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

    private ClusterNodeInformation getClusterNodeInformationFromSortedSetById(SortedSet<ClusterNodeInformation> clusterNodes, String nodeId) {
        for (ClusterNodeInformation clusterNodeInformation : clusterNodes.stream().toList()) {
            if (Objects.equals(clusterNodeInformation.id(), nodeId)) {
                return clusterNodeInformation;
            }
        }
        throw new RuntimeException("Node with id: " + nodeId + " not in set");
    }
}
