package store.messages;

import store.storeRecords.ClusterNodeInformation;
import store.storeRecords.MembershipEvent;
import store.Utils;

import java.util.*;

public class MembershipMessage extends Message {
    private final ArrayList<MembershipEvent> membershipLog;
    private final SortedSet<ClusterNodeInformation> clusterNodes;
    private final String ipAddress;
    public MembershipMessage(String id, int port, String ipAddress, List<MembershipEvent> membershipEvents, SortedSet<ClusterNodeInformation> clusterNodes) {
        super(id, port);
        this.ipAddress = ipAddress;
        this.membershipLog = new ArrayList<>(membershipEvents);
        this.clusterNodes = clusterNodes;
    }

    public SortedSet<ClusterNodeInformation> getClusterNodes() {
        return clusterNodes;
    }

    public List<MembershipEvent> getMembershipLog() {
        return membershipLog;
    }

    public String getIpAddress() {
        return ipAddress;
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
            stringBuilder.append(clusterNode.id()).append(" ")
                    .append(clusterNode.ipAddress()).append(" ")
                    .append(clusterNode.port()).append(" ")
                    .append(Utils.getAngle(clusterNode.id())).append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof MembershipMessage membershipMessage)) return false;
        return getPort() == membershipMessage.getPort() && getId().equals(membershipMessage.getId());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
