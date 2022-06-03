package store.messageHandlers;

import store.Store;
import store.Utils;
import store.messages.CheckReplicationMessage;
import store.messages.DeleteSuccessorMessage;
import store.messages.GetMessage;
import store.storeRecords.ClusterNodeInformation;
import store.storeRecords.Value;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class CheckReplicationMessageHandler extends MessageHandler<CheckReplicationMessage> {
    public CheckReplicationMessageHandler(Store store, CheckReplicationMessage message) {
        super(store, message, null);
    }

    @Override
    public void handle() throws NoSuchAlgorithmException, IOException {
        if (!getStore().getKeys().containsKey(getMessage().getKey())){
            ClusterNodeInformation nodeInformation =
                    Utils.getClosestNode(getStore().getClusterNodes().stream().toList(), Utils.getAngle(getMessage().getKey()));
            GetMessage getMessage = new GetMessage(nodeInformation.port(), getMessage().getKey());
            Socket socket = new Socket(nodeInformation.ipAddress(), nodeInformation.port());
            socket.getOutputStream().write(getMessage.toBytes());
            byte[] valueReceived = socket.getInputStream().readAllBytes();
            socket.close();
            getStore().put(getMessage().getKey(), Value.fromBytes(valueReceived));
        } else if (getStore().getKeys().get(getMessage().getKey())) {
            ClusterNodeInformation clusterNodeInformation =
                    Utils.getClusterNodeInformationFromSortedSetById(getStore().getClusterNodes(), getMessage().getId());
            Socket socket = new Socket(clusterNodeInformation.ipAddress(), clusterNodeInformation.port());
            getStore().sendTCP(new DeleteSuccessorMessage(getStore().getId(), getStore().getPort(), getMessage().getKey()), socket);
        }
    }
}
