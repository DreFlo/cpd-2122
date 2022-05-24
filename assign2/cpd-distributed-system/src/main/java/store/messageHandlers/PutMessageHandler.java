package store.messageHandlers;

import store.Store;
import store.Utils;
import store.messages.PutMessage;
import store.storeRecords.ClusterNodeInformation;

import java.io.IOException;
import java.net.Socket;
import java.util.AbstractMap;

public class PutMessageHandler extends MessageHandler<PutMessage> {
    public PutMessageHandler(Store store, PutMessage message) {
        super(store, message, null);
    }

    @Override
    public void handle() throws IOException {
        float keyAngle = Utils.getAngle(getMessage().getKey());
        ClusterNodeInformation nodeInformation =
                Utils.getClosestNode(getStore().getClusterNodes().stream().toList(), keyAngle);

        if(nodeInformation.id().equals(getStore().getId())){
            getStore().put(getMessage().getKey(), getMessage().getValue());

            ClusterNodeInformation firstSuccessor = Utils.sendSuccessorKey(
                    getStore(),
                    getStore().getId(),
                    new AbstractMap.SimpleEntry<String, byte[]>(getMessage().getKey(), getMessage().getValue()));

            if(firstSuccessor != null){
                Utils.sendSuccessorKey(
                        getStore(),
                        firstSuccessor.id(),
                        new AbstractMap.SimpleEntry<String, byte[]>(getMessage().getKey(), getMessage().getValue()));
            }

        }
        else {
            Socket socket = new Socket(nodeInformation.ipAddress(), nodeInformation.port());
            getStore().sendTCP(getMessage(), socket);
        }
    }
}
