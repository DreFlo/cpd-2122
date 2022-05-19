package store.storeRecords;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public record MembershipEvent(String nodeId, int membershipCounter) implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MembershipEvent)) return false;
        MembershipEvent that = (MembershipEvent) o;
        return membershipCounter == that.membershipCounter && nodeId.equals(that.nodeId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(membershipCounter);
        result = 31 * result + Arrays.hashCode(nodeId.toCharArray());
        return result;
    }
}
