package store.messages;

import java.util.Objects;

public class JoinLeaveMessage extends Message {
    int membershipCounter;
    private final String ipAddress;

    public JoinLeaveMessage(String id, int port, String ipAddress, int membershipCounter) {
        super(id, port);
        this.ipAddress = ipAddress;
        this.membershipCounter = membershipCounter;
    }

    public boolean isLeave() {
        return (this.membershipCounter % 2) == 1;
    }

    public int getMembershipCounter() {
        return membershipCounter;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public String toString() {
        return
                super.toString() +
                "Membership Counter - " + membershipCounter + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JoinLeaveMessage that)) return false;
        return getMembershipCounter() == that.getMembershipCounter() && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMembershipCounter());
    }
}
