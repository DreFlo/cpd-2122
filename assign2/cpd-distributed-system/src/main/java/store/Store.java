package store;

import store.messageHandlers.JoinLeaveMessageHandler;
import store.messages.JoinLeaveMessage;
import store.messages.MembershipMessage;
import store.messages.Message;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/* Create UDP Socket
* DatagramSocket sendSocket = new DatagramSocket(null);
* sendSocket.setReuseAddress(true);
* sendSocket.bind(new InetSocketAddress(storePort));
* sendSocket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, true);
* */

public class Store implements ClusterMembership, KeyValueStore<char[], Object> {
    private final char[] id;
    private final DatagramSocket clusterSocket;
    private final ServerSocket nodeSocket;
    private final Map<char[], char[]> keyNodeTable;
    private final InetSocketAddress group;
    private final int port;
    private final NetworkInterface networkInterface;
    private static final long STANDARD_TIMEOUT_SECONDS = 3;

    Store(char[] id, InetSocketAddress group, int storePort) throws IOException {
        this.id = id;
        this.port = storePort;
        this.clusterSocket = new DatagramSocket(null);
        this.clusterSocket.setReuseAddress(true);
        this.clusterSocket.bind(new InetSocketAddress(group.getPort()));
        this.clusterSocket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, true);

        this.nodeSocket = new ServerSocket(port);

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

            store.join();
            store.listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public char[] getId() {
        return id;
    }

    public DatagramSocket getClusterSocket() {
        return clusterSocket;
    }

    public ServerSocket getNodeSocket() {
        return nodeSocket;
    }

    public Map<char[], char[]> getKeyNodeTable() {
        return keyNodeTable;
    }

    public InetSocketAddress getGroup() {
        return group;
    }

    public NetworkInterface getNetworkInterface() {
        return networkInterface;
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
            clusterSocket.joinGroup(group, networkInterface);

            Set<MembershipMessage> messages = new HashSet<>();

            for (int i = 0; i < 3; i++) {
                sendUDP(new JoinLeaveMessage(id, getPort(), getMembershipCounter()), group);

                ExecutorService executorService = Executors.newSingleThreadExecutor();

                var future = executorService.submit(() -> {
                    while (true) {
                        try {
                            Socket socket;
                            socket = nodeSocket.accept();
                            InputStream inputStream = socket.getInputStream();
                            byte[] bytes = new byte[4096];
                            int read = inputStream.read(bytes);
                            messages.add((MembershipMessage) Message.fromBytes(bytes));
                            if (messages.size() > 2) return;
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                try {
                    future.get(STANDARD_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (TimeoutException e) {
                    System.out.println("Timeout");
                }

                System.out.println(messages);

                if (messages.size() > 2) break;
            }

            incrementMembershipCounter();
            System.out.println("\nJoined\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void leave() {
        try {
            // TODO Send leave message
            clusterSocket.leaveGroup(group, networkInterface);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listen() throws IOException, ClassNotFoundException {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        while (true) {
            DatagramPacket datagramPacket = new DatagramPacket(new byte[4096], 4096);
            clusterSocket.receive(datagramPacket);
            Message message = Message.fromBytes(datagramPacket.getData());
            System.out.println("RECEIVED:\n" + message + "\n");
            executorService.submit(new JoinLeaveMessageHandler(this, (JoinLeaveMessage) message));
        }
    }

    /**
     * Sends message to address
     */
    public void sendUDP(Message message, SocketAddress address) throws IOException {
        DatagramSocket sendSocket = new DatagramSocket(null);
        sendSocket.setReuseAddress(true);
        sendSocket.bind(new InetSocketAddress(this.getPort()));
        sendSocket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, true);
        byte[] message_ = (message.toBytes());
        DatagramPacket datagramPacket = new DatagramPacket(message_, message_.length, address);
        sendSocket.send(datagramPacket);
        sendSocket.close();
        System.out.println("SENT BY UDP:\n" + message + "\n");
    }

    /**
     * Sends message through socket and closes socket
     */
    public void sendTCP(Message  message, Socket socket) throws IOException {
        socket.getOutputStream().write(message.toBytes());
        socket.close();
        System.out.println("SENT BY TCP:\n" + message + "\n");
    }

    public Integer getPort() {
        return this.port;
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
