package store.messages;

import store.storeRecords.ClusterNodeInformation;
import store.storeRecords.MembershipEvent;
import store.Utils;

import java.util.*;

public class MembershipMessage extends Message {
    private final ArrayList<MembershipEvent> membershipLog;
    private final SortedSet<ClusterNodeInformation> clusterNodes;
    public MembershipMessage(String id, int port, List<MembershipEvent> membershipEvents, SortedSet<ClusterNodeInformation> clusterNodes) {
        super(id, port);
        this.membershipLog = new ArrayList<>(membershipEvents);
        this.clusterNodes = clusterNodes;
    }

    public SortedSet<ClusterNodeInformation> getClusterNodes() {
        return clusterNodes;
    }

    public List<MembershipEvent> getMembershipLog() {
        return membershipLog;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        stringBuilder.append("Membership Log:\n");
        for (MembershipEvent membershipEvent : membershipLog) {
            stringBuilder.append(membershipEvent.nodeId()).append(" ").append(membershipEvent.membershipCounter())
                    .append("\n");
        }
        stringBuilder.append("Cluster Nodes:\n");
        for (ClusterNodeInformation clusterNode : clusterNodes) {
            stringBuilder.append(clusterNode.id()).append(" ").append(clusterNode.port()).append(" ")
                    .append(Utils.getAngle(clusterNode.id())).append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
