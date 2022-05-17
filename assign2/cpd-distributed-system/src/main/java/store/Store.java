package store;

import jdk.jshell.spi.ExecutionControl;
import store.messageHandlers.MessageHandlerBuilder;
import store.messages.JoinLeaveMessage;
import store.messages.MembershipMessage;
import store.messages.Message;
import store.storeRecords.ClusterNodeInformation;
import store.storeRecords.MembershipEvent;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Store implements ClusterMembership, KeyValueStore<char[], Object> {
    private static final long STANDARD_TIMEOUT_SECONDS = 3;
    private final char[] id;
    private final DatagramSocket clusterSocket;
    private final ServerSocket nodeSocket;
    private final Map<char[], char[]> keyNodeTable;
    private final InetSocketAddress group;
    private final int port;
    private final NetworkInterface networkInterface;
    private final Stack<Message> sentMessages;
    private final Stack<Message> handledReceivedMessages;
    private final List<MembershipEvent> membershipEvents;
    private Set<ClusterNodeInformation> clusterNodes;

    Store(char[] id, InetSocketAddress group, int storePort) throws IOException {
        this.id = id;

        initializeDirectory();

        this.port = storePort;

        this.clusterSocket = new DatagramSocket(null);
        this.clusterSocket.setReuseAddress(true);
        this.clusterSocket.bind(new InetSocketAddress(group.getPort()));
        this.clusterSocket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, true);

        this.nodeSocket = new ServerSocket(port);

        this.group = group;
        this.keyNodeTable = new HashMap<>();
        this.networkInterface = NetworkInterface.getByName("lo");

        this.sentMessages = new Stack<>();
        this.handledReceivedMessages = new Stack<>();

        this.membershipEvents = new ArrayList<>();
        initializeMembershipEvents();

        this.clusterNodes = new HashSet<>();
        this.clusterNodes.add(new ClusterNodeInformation(getId(), getPort()));
    }

    public static void main(String[] args) {
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

    private void initializeDirectory() {
        File directory = new File(Utils.keyToString(id));
        if (directory.mkdir()) {
            System.out.println("Created new directory");
        } else {
            System.out.println("Directory already existed");
        }
    }

    private void initializeMembershipEvents() throws IOException {
        File file = new File(Utils.keyToString(id) + "\\" + Utils.keyToString(id) + "_membership_log");

        if (file.createNewFile()) {
            System.out.println("Created membership log");
        } else {
            System.out.println("Opened membership log");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                List<String> splitLine = List.of(line.split(" "));
                this.membershipEvents.add(new MembershipEvent(Utils.stringToKey(splitLine.get(0)), Integer.parseInt(splitLine.get(1))));
            }
            scanner.close();
        }
        // TODO PAY ATTENTION TO LIST
        addMembershipEvent(new MembershipEvent(getId(), getMembershipCounter()));

    }

    public boolean addMembershipEvent(MembershipEvent newMembershipEvent) {
        MembershipEvent oldMembershipEvent = checkMembershipEventInLog(newMembershipEvent);
        if (oldMembershipEvent == null) {
            this.membershipEvents.add(newMembershipEvent);
            return true;
        }
        else if (oldMembershipEvent.membershipCounter() < newMembershipEvent.membershipCounter()) {
            this.membershipEvents.remove(oldMembershipEvent);
            this.membershipEvents.add(newMembershipEvent);
            File file = new File(Utils.keyToString(id) + "\\" + Utils.keyToString(id) + "_membership_log");
            try (Writer writer = new FileWriter(file)) {
                for (MembershipEvent membershipEvent : this.membershipEvents) {
                    writer.write(Utils.keyToString(membershipEvent.nodeId()).concat(" ").concat(Integer.toString(membershipEvent.membershipCounter())).concat("\n"));
                }
            } catch (IOException e) {
                System.out.println("Could not register new membership event");
            }
            return true;
        }
        return false;
    }

    private MembershipEvent checkMembershipEventInLog(MembershipEvent newEvent) {
        for (MembershipEvent membershipEvent : membershipEvents) {
            if (Arrays.equals(membershipEvent.nodeId(), newEvent.nodeId())) {
                return membershipEvent;
            }
        }
        return null;
    }

    public List<MembershipEvent> getMostRecentMembershipEvents() {
        return this.membershipEvents.subList(Math.max(0, this.membershipEvents.size() - 32), this.membershipEvents.size());
    }

    public void removeNodeFromClusterRecord(ClusterNodeInformation clusterNodeInformation) {
        this.clusterNodes.remove(clusterNodeInformation);
    }

    public void addNodeToClusterRecord(ClusterNodeInformation clusterNodeInformation) {
        this.clusterNodes.add(clusterNodeInformation);
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

    public Stack<Message> getSentMessages() {
        return sentMessages;
    }

    public Stack<Message> getHandledReceivedMessages() {
        return handledReceivedMessages;
    }

    public List<MembershipEvent> getMembershipEvents() {
        return membershipEvents;
    }

    public Set<ClusterNodeInformation> getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(Set<ClusterNodeInformation> clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public void incrementMembershipCounter() throws IOException {
        int membershipCounter = getMembershipCounter();
        membershipCounter++;
        Writer writer = new FileWriter(Utils.keyToString(id) + "\\" + Utils.keyToString(id) + "_membership_counter");
        writer.write(Integer.toString(membershipCounter));
        writer.close();
    }

    public int getMembershipCounter() throws IOException {
        int membershipCounter = 0;
        File file = new File(Utils.keyToString(id) + "\\" + Utils.keyToString(id) + "_membership_counter");

        if (file.createNewFile()) {
            Writer writer = new FileWriter(file);
            writer.write(Integer.toString(membershipCounter));
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

                Future<?> future = executorService.submit(() -> {
                    while (true) {
                        try {
                            Message message = receive(nodeSocket);
                            if (message instanceof MembershipMessage membershipMessage) {
                                messages.add(membershipMessage);
                            }
                            if (messages.size() > 2) return;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                try {
                    future.get(STANDARD_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (TimeoutException e) {
                    System.out.println("Timeout number " + (i + 1));
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

    public void listen() throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(21);

        // Ping broadcast with membership every 1 second
        executorService.submit(new MembershipPing(this));

        // Receive messages
        while (true) {
            Message message = receive(clusterSocket);
            System.out.println("RECEIVED:\n" + message + "\n");
            try {
                executorService.submit(MessageHandlerBuilder.get(this, message));
            } catch (ExecutionControl.NotImplementedException e) {
                System.out.println("No handler found: " + e);
            }
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
        sentMessages.push(message);
        System.out.println("SENT BY UDP:\n\n" + message + "\n\n");
    }

    /**
     * Sends message through socket and closes socket
     */
    public void sendTCP(Message message, Socket socket) throws IOException {
        socket.getOutputStream().write(message.toBytes());
        socket.close();
        sentMessages.push(message);
        System.out.println("SENT BY TCP:\n\n" + message + "\n\n");
    }

    /**
     * Reads a message from a ServerSocket
     */
    public Message receive(ServerSocket serverSocket) throws IOException {
        Socket socket;
        socket = serverSocket.accept();
        InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[4096];
        int read = inputStream.read(bytes);
        return Message.fromBytes(bytes);
    }

    /**
     * Reads a message from a DatagramSocket
     */
    public Message receive(DatagramSocket datagramSocket) throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(new byte[4096], 4096);
        datagramSocket.receive(datagramPacket);
        return Message.fromBytes(datagramPacket.getData());
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
