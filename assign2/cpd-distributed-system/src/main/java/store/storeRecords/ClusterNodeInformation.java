package store.storeRecords;

import java.io.Serializable;
import java.util.Objects;

public record ClusterNodeInformation(String id, String ipAddress, int port, float angle) implements Serializable, Comparable<ClusterNodeInformation> {
    @Override
    public int compareTo(ClusterNodeInformation o) {
        if(this.angle < o.angle) return -1;
        else if(this.angle == o.angle) return 0;
        else return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClusterNodeInformation that)) return false;
        return Float.compare(that.angle, angle) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(angle);
    }
}
