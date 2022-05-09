import java.io.IOException;
import java.net.*;

public class Store implements ClusterMembership {
    private final int id;
    private final DatagramSocket socket;

    Store(int id, DatagramSocket socket) {
        this.id = id;
        this.socket = socket;
    }

    public static void main(String[] args) {
        try {
            InetAddress IP_mcast_addr = InetAddress.getByName(args[0]);
            int IP_mcast_port = Integer.parseInt(args[1]);
            int node_id = Integer.parseInt(args[2]);
            int Store_port = Integer.parseInt(args[3]);
            InetSocketAddress group = new InetSocketAddress(IP_mcast_addr, IP_mcast_port);

            DatagramSocket socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(Store_port));

            NetworkInterface netIf = NetworkInterface.getByName("eth0");

            socket.joinGroup(group, netIf);

            Store store = new Store(node_id, socket);

            store.listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void join() {

    }

    @Override
    public void leave() {

    }

    public void listen() {

    }
}
