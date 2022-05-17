package store.messages;

import java.util.Objects;

public class JoinLeaveMessage extends Message {
    int membershipCounter;

    public JoinLeaveMessage(char[] id, int port, int membershipCounter) {
        super(id, port);
        this.membershipCounter = membershipCounter;
    }

    public boolean isLeave() {
        return (this.membershipCounter % 2) == 1;
    }

    public int getMembershipCounter() {
        return membershipCounter;
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
