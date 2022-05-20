package store.storeRecords;

import java.io.Serializable;

public record ClusterNodeInformation(String id, String ipAddress, int port, float angle) implements Serializable, Comparable<ClusterNodeInformation> {
    @Override
    public int compareTo(ClusterNodeInformation o) {
        if(this.angle < o.angle) return -1;
        else if(this.angle == o.angle) return 0;
        else return 1;
    }
}
