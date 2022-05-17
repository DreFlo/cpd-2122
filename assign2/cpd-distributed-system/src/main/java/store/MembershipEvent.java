package store;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public record MembershipEvent(char[] nodeId, int membershipCounter) implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MembershipEvent)) return false;
        MembershipEvent that = (MembershipEvent) o;
        return membershipCounter == that.membershipCounter && Arrays.equals(nodeId, that.nodeId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(membershipCounter);
        result = 31 * result + Arrays.hashCode(nodeId);
        return result;
    }
}
