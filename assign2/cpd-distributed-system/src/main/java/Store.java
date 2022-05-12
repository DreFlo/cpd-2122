import java.io.*;
import java.net.*;
import java.util.*;

public class Store implements ClusterMembership, KeyValueStore<char[], Object> {
    private final char[] id;
    private final DatagramSocket listenSocket;
    private final DatagramSocket sendSocket;
    private final Map<char[], char[]> keyNodeTable;
    private final InetSocketAddress group;

    private NetworkInterface networkInterface;

    private static final boolean DEBUG = true;

    private static boolean receiver;

    Store(char[] id, InetSocketAddress group, int storePort) throws IOException {
        this.id = id;
        this.listenSocket = new DatagramSocket(null);
        listenSocket.setReuseAddress(true);
        listenSocket.bind(new InetSocketAddress(group.getPort()));
        listenSocket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, true);
        this.sendSocket = new DatagramSocket(null);
        sendSocket.setReuseAddress(true);
        sendSocket.bind(new InetSocketAddress(storePort));
        sendSocket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, true);
        this.group = group;
        this.keyNodeTable = new HashMap<>();
        this.networkInterface = NetworkInterface.getByName("lo");
    }

    public static void main(String[] args) throws ClassNotFoundException {
        try {
            InetAddress IP_mcast_addr = InetAddress.getByName(args[0]);
            int IP_mcast_port = Integer.parseInt(args[1]);
            int Store_port = Integer.parseInt(args[3]);
            InetSocketAddress group = new InetSocketAddress(IP_mcast_addr, IP_mcast_port);

            Store store = new Store(Utils.stringToKey(args[2]), group, Store_port);

            if (DEBUG) {
                receiver = args[4].equals("R");
            }

            store.listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void incrementMembershipCounter() throws IOException {
        Integer membershipCounter = getMembershipCounter();
        membershipCounter++;
        Writer writer = new FileWriter(Utils.keyToString(id) + "\\" + Utils.keyToString(id) + "_membership_counter");
        writer.write(membershipCounter.toString());
        writer.close();
    }

    public int getMembershipCounter() throws IOException {
        Integer membershipCounter = 0;
        File directory = new File(Utils.keyToString(id));
        File file = new File(Utils.keyToString(id) + "\\" + Utils.keyToString(id) + "_membership_counter");

        if (directory.mkdir()) {
            file.createNewFile();
            Writer writer = new FileWriter(file);
            writer.write(membershipCounter.toString());
            writer.close();
        } else {
            Scanner scanner = new Scanner(file);
            membershipCounter = Integer.parseInt(scanner.nextLine());
        }

        return membershipCounter;
    }

    @Override
    public void join() {
        try {
            listenSocket.joinGroup(group, networkInterface);
            send(new JoinLeaveMessage(id, getPort(), getMembershipCounter()));

            Set<MembershipMessage> messages = new HashSet<>();

            while (true) {
                DatagramPacket packet = new DatagramPacket(new byte[4096], 4096);
                sendSocket.receive(packet);
                messages.add((MembershipMessage) Message.fromBytes(packet.getData()));
                if (messages.size() > 2) break;
            }

            incrementMembershipCounter();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void leave() {
        try {
            // TODO Send leave message
            listenSocket.leaveGroup(group, networkInterface);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listen() throws IOException {
        join();
    }

    public void send(JoinLeaveMessage message) throws IOException {
        byte[] message_ = (message.toBytes());
        DatagramPacket datagramPacket = new DatagramPacket(message_, message_.length, group);
        sendSocket.send(datagramPacket);
    }

    public Integer getPort() {
        return sendSocket.getPort();
    }

    @Override
    public void put(char[] key, Object value) {
    }

    @Override
    public Object get(char[] key) {
        return null;
    }

    @Override
    public void delete(char[] key) {

    }
}
