package store.messages;

import store.ClusterNodeInformation;
import store.MembershipEvent;
import store.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MembershipMessage extends Message {
    private final ArrayList<MembershipEvent> membershipLog;
    private final HashSet<ClusterNodeInformation> clusterNodes;
    public MembershipMessage(char[] id, int port, List<MembershipEvent> membershipEvents, Set<ClusterNodeInformation> clusterNodes) {
        super(id, port);
        this.membershipLog = new ArrayList<>(membershipEvents);
        this.clusterNodes = new HashSet<>(clusterNodes);
    }

    public Set<ClusterNodeInformation> getClusterNodes() {
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
            stringBuilder.append(Utils.keyToString(membershipEvent.nodeId())).append(" ").append(membershipEvent.membershipCounter()).append("\n");
        }
        stringBuilder.append("Cluster Nodes:\n");
        for (ClusterNodeInformation clusterNode : clusterNodes) {
            stringBuilder.append(Utils.keyToString(clusterNode.id())).append(" ").append(clusterNode.port()).append("\n");
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
