package store;

import jdk.jshell.spi.ExecutionControl;
import store.messageHandlers.MessageHandlerBuilder;
import store.messageHandlers.TestJoinMessageHandler;
import store.messages.*;
import store.storeRecords.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public class Store implements ClusterMembership, KeyValueStore<String, Value> {
    private static final long STANDARD_TIMEOUT_SECONDS = 3;
    private final String id;
    private final DatagramSocket clusterSocket;
    private final HashMap<String, Boolean> keys;
    private final InetSocketAddress group;
    private final int port;
    private final String ipAddress;
    private final NetworkInterface networkInterface;
    private final Stack<Message> sentMessages;
    private final Stack<Message> handledReceivedMessages;
    private final List<MembershipEvent> membershipEvents;
    private ServerSocket nodeSocket;
    private SortedSet<ClusterNodeInformation> clusterNodes;
    private ExecutorService executorService = null;

    private Instant lastMembershipUpdateTime;

    Store(String id, InetSocketAddress group, int storePort, String ipAddress) throws IOException {
        this.id = id;

        initializeDirectory();

        this.port = storePort;
        this.ipAddress = ipAddress;

        this.clusterSocket = new DatagramSocket(null);
        this.clusterSocket.setReuseAddress(true);
        this.clusterSocket.bind(new InetSocketAddress(group.getPort()));
        this.clusterSocket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, true);

        this.nodeSocket = new ServerSocket(getPort(), 50, InetAddress.getByName(getIpAddress()));

        this.group = group;
        this.keys = new HashMap<>();
        this.networkInterface = NetworkInterface.getByName("lo");

        this.sentMessages = new Stack<>();
        this.handledReceivedMessages = new Stack<>();

        this.membershipEvents = new ArrayList<>();
        initializeMembershipEvents();

        this.clusterNodes = new TreeSet<>();
        addNodeToClusterRecord(new ClusterNodeInformation(getId(), getIpAddress(), getPort(), Utils.getAngle(getId())));
        System.out.println("\n\nAngle:" + Utils.getAngle(getId()) + "\nHash: " + getId() + "\n");
        this.lastMembershipUpdateTime = Instant.now();
    }

    public static void main(String[] args) {
        try {
            InetAddress IP_mcast_addr = InetAddress.getByName(args[0]);
            int IP_mcast_port = Integer.parseInt(args[1]);
            int Store_port = Integer.parseInt(args[3]);
            InetSocketAddress group = new InetSocketAddress(IP_mcast_addr, IP_mcast_port);

            String string = args[2] + args[3];

            Store store = new Store(Utils.hash(string.getBytes(StandardCharsets.UTF_8)), group, Store_port, args[2]);

            store.awaitTestClientJoin();
        } catch (IOException | ExecutionControl.NotImplementedException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeDirectory() {
        File directory = new File(id);
        if (directory.mkdir()) {
            System.out.println("Created new directory");
        } else {
            System.out.println("Directory already existed");
        }
    }

    private void awaitTestClientJoin() throws IOException, ExecutionControl.NotImplementedException {
        while (true) {
            loadServerSocket();
            Message message = receive(nodeSocket).getKey();
            if (message instanceof TestJoinMessage testJoinMessage) {
                TestJoinMessageHandler testJoinMessageHandler = (TestJoinMessageHandler) MessageHandlerBuilder.get(this, testJoinMessage, null);
                testJoinMessageHandler.run();
                break;
            }
        }
    }

    private void initializeMembershipEvents() throws IOException {
        File file = new File(id + "\\" + "membership_log");

        if (file.createNewFile()) {
            System.out.println("Created membership log");
        } else {
            System.out.println("Opened membership log");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                List<String> splitLine = List.of(line.split(" "));
                this.membershipEvents.add(new MembershipEvent(splitLine.get(0), Integer.parseInt(splitLine.get(1))));
            }
            scanner.close();
        }
        addMembershipEvent(new MembershipEvent(getId(), getMembershipCounter()));

    }

    public boolean addMembershipEvent(MembershipEvent newMembershipEvent) {
        lastMembershipUpdateTime = Instant.now();
        MembershipEvent oldMembershipEvent = checkMembershipEventInLog(newMembershipEvent);
        if (oldMembershipEvent == null) {
            this.membershipEvents.add(newMembershipEvent);
            return true;
        } else if (oldMembershipEvent.membershipCounter() < newMembershipEvent.membershipCounter()) {
            this.membershipEvents.remove(oldMembershipEvent);
            this.membershipEvents.add(newMembershipEvent);
            File file = new File(id + "\\" + "membership_log");
            try (Writer writer = new FileWriter(file)) {
                for (MembershipEvent membershipEvent : this.membershipEvents) {
                    writer.write(membershipEvent.nodeId().concat(" ").concat(Integer.toString(membershipEvent.membershipCounter())).concat("\n"));
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
            if (membershipEvent.nodeId().equals(newEvent.nodeId())) {
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

    public String getId() {
        return id;
    }

    public DatagramSocket getClusterSocket() {
        return clusterSocket;
    }

    public ServerSocket getNodeSocket() {
        return nodeSocket;
    }

    public HashMap<String, Boolean> getKeys() {
        return keys;
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

    public SortedSet<ClusterNodeInformation> getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(SortedSet<ClusterNodeInformation> clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public Store getThis() {
        return this;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void incrementMembershipCounter() throws IOException {
        int membershipCounter = getMembershipCounter();
        membershipCounter++;
        Writer writer = new FileWriter(id + "\\" + "membership_counter");
        writer.write(Integer.toString(membershipCounter));
        writer.close();
    }

    public int getMembershipCounter() throws IOException {
        int membershipCounter = 0;
        File file = new File(id + "\\" + "membership_counter");

        if (file.createNewFile()) {
            Writer writer = new FileWriter(file);
            writer.write(Integer.toString(membershipCounter));
            writer.close();
        } else {
            Scanner scanner = new Scanner(file);
            membershipCounter = Integer.parseInt(scanner.nextLine());
        }

        System.out.println("\n\nMembership counter - " + membershipCounter + "\n\n");

        return membershipCounter;
    }

    @Override
    public void join() {
        try {
            clusterSocket.joinGroup(group, networkInterface);

            Set<MembershipMessage> messages = new HashSet<>();

            for (int i = 0; i < 3; i++) {
                loadServerSocket();
                sendUDP(new JoinLeaveMessage(id, getPort(), getIpAddress(), getMembershipCounter()), group);

                ExecutorService executorService = Executors.newSingleThreadExecutor();

                Future<?> future = executorService.submit(() -> {
                    while (true) {
                        try {
                            Message message = receive(nodeSocket).getKey();
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
                    nodeSocket.close();
                    executorService.shutdownNow();
                } catch (TimeoutException e) {
                    nodeSocket.close();
                    future.cancel(true);
                    executorService.shutdownNow();
                    System.out.println("Timeout number " + (i + 1));
                } catch (ExecutionException e) {

                } catch (InterruptedException e) {

                }

                System.out.println(messages);
                System.out.println(handledReceivedMessages);

                if (messages.size() > 2) break;
            }

            incrementMembershipCounter();
            System.out.println("\nJoined\n");
            for (MembershipMessage membershipMessage : messages) {
                MessageHandlerBuilder.get(getThis(), membershipMessage, null).run();
            }
            getStartingKeyValues();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExecutionControl.NotImplementedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void leave() {
        try {
            executorService.shutdownNow();
            LeaveKeyTransferMessage leaveKeyTransferMessage = new LeaveKeyTransferMessage(getId(), getPort(), getKeyValues());
            ClusterNodeInformation successor = Utils.getSuccessor(getClusterNodes().stream().toList(), getId());
            Socket socket = new Socket(successor.ipAddress(), successor.port());
            sendTCP(leaveKeyTransferMessage, socket);
            this.removeAllKeyFiles();
            sendUDP(new JoinLeaveMessage(getId(), getPort(), getIpAddress(), getMembershipCounter()), getGroup());
            incrementMembershipCounter();
            clusterSocket.leaveGroup(group, networkInterface);
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listen() throws IOException {
        executorService = Executors.newFixedThreadPool(4);

        // Ping broadcast with membership every 1 second
        executorService.submit(checkReplication());
        executorService.submit(new MembershipPing(this));
        executorService.submit(listenUDP());
        executorService.submit(listenTCP());
    }

    public Runnable listenUDP() {
        ExecutorService executorService = Executors.newFixedThreadPool(9);
        return () -> {
            while (true) {
                Message message = null;
                try {
                    message = receive(clusterSocket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //System.out.println("RECEIVED:\n" + message + "\n");
                try {
                    executorService.submit(MessageHandlerBuilder.get(getThis(), message, null));
                } catch (ExecutionControl.NotImplementedException e) {
                    System.out.println("No handler found: " + e);
                }
            }
        };
    }

    public void loadServerSocket() throws IOException {
        if (this.nodeSocket != null) {
            this.nodeSocket.close();
        }
        this.nodeSocket = new ServerSocket(getPort(), 50, InetAddress.getByName(getIpAddress()));
    }

    public Runnable listenTCP() throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(9);
        return () -> {
            while (true) {
                AbstractMap.SimpleEntry<Message, Socket> entry;
                try {
                    loadServerSocket();
                    entry = receive(getNodeSocket());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //System.out.println("RECEIVED:\n" + entry.getKey() + "\n");
                try {
                    executorService.submit(MessageHandlerBuilder.get(getThis(), entry.getKey(), entry.getValue()));
                } catch (ExecutionControl.NotImplementedException e) {
                    System.out.println("No handler found: " + e);
                }
            }
        };
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
        //System.out.println("SENT BY UDP:\n\n" + message + "\n\n");
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
    public AbstractMap.SimpleEntry<Message, Socket> receive(ServerSocket serverSocket) throws IOException {
        Socket socket;
        socket = serverSocket.accept();
        InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[4096];
        int read = inputStream.read(bytes);
        Message message = Message.fromBytes(bytes);
        return new AbstractMap.SimpleEntry<>(message, socket);
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

    public HashMap<String, Value> getKeyValues() throws IOException {
        HashMap<String, Value> keyValues = new HashMap<>();
        for (String key : getKeys().keySet()) {
            Value value = get(key);
            keyValues.put(key, value);
        }
        return keyValues;
    }

    /**
     * After a node joins the cluster it gets the keys from:
     *  - files that were already in storage
     *  - another node which would now split its keys
     */
    private void getStartingKeyValues() throws IOException {
        File file = new File(id);
        String[] pathNames = file.list();
        if(pathNames.length > 2){
            for(String pathname : pathNames){
                if(pathname.contains("membership"))
                    continue;
                else {
                    this.keys.put(pathname, Utils.isTombstone(getId(), pathname));
                }
            }
        }

        JoinKeyTransferMessage joinKeyTransferMessage = new JoinKeyTransferMessage(getId(), getPort());

        ClusterNodeInformation successor = Utils.getSuccessor(getClusterNodes().stream().toList(), getId());
        if (successor.id().equals(getId())) return;
        Socket socket = new Socket(successor.ipAddress(), successor.port());
        socket.getOutputStream().write(joinKeyTransferMessage.toBytes());

        byte[] received = socket.getInputStream().readAllBytes();
        socket.close();
        JoinKeyTransferMessage receivedMessage = (JoinKeyTransferMessage) Message.fromBytes(received);
        for (Map.Entry<String, Value> keyValue : receivedMessage.getKeyValues().entrySet()) {
            put(keyValue.getKey(), keyValue.getValue());
        }
    }

    private Runnable checkReplication() {
        return () -> {
            while(true){
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for(ClusterNodeInformation clusterNodeInformation : getClusterNodes()){
                    System.out.println("IN REP NODE: " + clusterNodeInformation.id());
                }
                Map<String, Boolean> keys = getKeys();
                Store currentStore = getThis();
                for(Map.Entry<String, Boolean> key : keys.entrySet()){
                    if(!key.getValue()) {
                        List<ClusterNodeInformation> threeNodes = Utils.getThreeNodesForKey(currentStore, key.getKey());
                        CheckReplicationMessage checkReplicationMessage = new CheckReplicationMessage(getId(), getPort(), key.getKey());
                        for (ClusterNodeInformation clusterNodeInformation : threeNodes) {
                            if (!clusterNodeInformation.id().equals(getId())) {
                                Socket socket;
                                try {
                                    socket = new Socket(clusterNodeInformation.ipAddress(), clusterNodeInformation.port());
                                    sendTCP(checkReplicationMessage, socket);
                                    System.out.println("SENT REPLICATION: " + clusterNodeInformation.id() + "\n");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }

            }
        };

    }

    public void removeKeyFile(String key){
        File keyFile = new File(id + "\\" + key);
        keyFile.delete();
        this.keys.remove(key);
    }
    public void removeAllKeyFiles(){
        Set<String> keys = getKeys().keySet();
        for(String key : keys){
            this.removeKeyFile(key);
        }
    }

    @Override
    public String put(String key, Value value) throws IOException {
        File file = new File(id + "\\" + key);
        if (!file.createNewFile()){
            if(!getKeys().get(key))
                return "File already existed.";
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(value.toBytes());
        fileOutputStream.close();
        this.keys.put(key, value instanceof TombstoneValue);
        return "Put was successful.";
    }

    @Override
    public Value get(String key) throws IOException {
        if(!getKeys().containsKey(key)) return new NullValue();
        if(getKeys().get(key)) return new TombstoneValue();
        ClassLoader classLoader = Store.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(id + "\\" + key);
        byte[] value = inputStream.readAllBytes();
        inputStream.close();
        return Value.fromBytes(value);
    }

    @Override
    public String delete(String key) throws IOException {
        if(!getKeys().containsKey(key)) return "File doesn't exist";
        if(getKeys().get(key)) return "File already deleted.";
        File keyFile = new File(id + "\\" + key);
        FileOutputStream fileOutputStream = new FileOutputStream(keyFile);
        fileOutputStream.write((new TombstoneValue()).toBytes());
        fileOutputStream.close();
        this.keys.replace(key, false, true);
        return "Delete was successful.";
    }

    public void stopListening() {
        executorService.shutdownNow();
    }

    public boolean isStaleNode() {
        if (getClusterNodes().size() == 1) return false;
        return Duration.between(lastMembershipUpdateTime, Instant.now()).compareTo(Duration.ofSeconds(3L * getClusterNodes().size())) > 0;
    }
}
