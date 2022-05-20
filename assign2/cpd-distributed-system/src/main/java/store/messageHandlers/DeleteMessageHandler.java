package store.messageHandlers;

import store.Store;
import store.Utils;
import store.messages.DeleteMessage;
import store.storeRecords.ClusterNodeInformation;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class DeleteMessageHandler extends MessageHandler<DeleteMessage> {
    public DeleteMessageHandler(Store store, DeleteMessage message) {
        super(store, message, null);
    }

    @Override
    public void handle() throws IOException {
        float keyAngle = Utils.getAngle(getMessage().getKey());
        ClusterNodeInformation nodeInformation =
                Utils.getClosestNode(getStore().getClusterNodes().stream().toList(), keyAngle);

        if(nodeInformation.id().equals(getStore().getId())){
            getStore().delete(getMessage().getKey());
        }
        else {
            Socket socket = new Socket(nodeInformation.ipAddress(), nodeInformation.port());
            getStore().sendTCP(getMessage(), socket);
        }
    }
}
