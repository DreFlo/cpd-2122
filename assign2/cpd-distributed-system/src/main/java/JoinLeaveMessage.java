import java.util.Arrays;

public class JoinLeaveMessage extends Message {
    JoinLeaveMessage(char[] nodeId, int port, int membershipCounter) {
        super(getHeaderMsg(nodeId, port, membershipCounter), null);
    }

    private static String getHeaderMsg(char[] nodeId, int port, int membershipCounter) {
        return getHeaderMsg(nodeId, port) +
                "TYPE:\tJOIN/LEAVE\n" +
                "MEMBERSHIP_COUNTER:\t" + membershipCounter;
    }
}
