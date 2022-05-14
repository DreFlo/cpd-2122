package store.messages;

public class JoinLeaveMessage extends Message {
    int membershipCounter;

    public JoinLeaveMessage(char[] id, int port, int membershipCounter) {
        super(getHeaderMsg(id, port, membershipCounter), null, id, port);
        this.membershipCounter = membershipCounter;
    }

    private static String getHeaderMsg(char[] nodeId, int port, int membershipCounter) {
        return getHeaderMsg(nodeId, port) +
                "TYPE\tJOIN/LEAVE\n" +
                "MEMBERSHIP_COUNTER\t" + membershipCounter;
    }

    public boolean isLeave() {
        return (this.membershipCounter % 2) == 1;
    }
}
