package store;

import java.io.Serializable;

public record ClusterNodeInformation(char[] id, int port) implements Serializable {
}