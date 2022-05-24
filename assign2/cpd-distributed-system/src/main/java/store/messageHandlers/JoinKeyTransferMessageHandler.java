package store.messageHandlers;

import jdk.jshell.execution.Util;
import store.Store;
import store.Utils;
import store.messages.JoinKeyTransferMessage;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Set;

public class JoinKeyTransferMessageHandler extends MessageHandler<JoinKeyTransferMessage> {
    public JoinKeyTransferMessageHandler(Store store, JoinKeyTransferMessage message, Socket responseSocket) {
        super(store, message, responseSocket);
    }

    @Override
    public void handle() throws NoSuchAlgorithmException, IOException {
        HashMap<String, byte[]> keyValues = new HashMap<>();
        float receivedAngle = Utils.getAngle(getMessage().getId());
        float storeAngle = Utils.getAngle(getStore().getId());
        for(String key : getStore().getKeys().stream().toList()){
            float keyAngle = Utils.getAngle(key);
            if(keyAngle <= receivedAngle || keyAngle > storeAngle){
                keyValues.put(key, getStore().get(key));
                getStore().delete(key);
            }
        }
        getMessage().setKeyValues(keyValues);
        getResponseSocket().getOutputStream().write(getMessage().toBytes());
        getResponseSocket().close();
    }
}
