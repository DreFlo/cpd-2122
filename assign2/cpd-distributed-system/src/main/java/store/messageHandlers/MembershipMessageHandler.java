package store.messageHandlers;

import store.Store;
import store.messages.MembershipMessage;
import store.storeRecords.MembershipEvent;

public class MembershipMessageHandler extends MessageHandler<MembershipMessage> {
    public MembershipMessageHandler(Store store, MembershipMessage message) {
        super(store, message, null);
    }

    @Override
    public void handle() {
        MembershipMessage membershipMessage = getMessage();
        Store store = getStore();

        boolean moreRecent = false;
        for (MembershipEvent receivedMembershipEvent : membershipMessage.getMembershipLog()) {
            if (store.addMembershipEvent(receivedMembershipEvent)) moreRecent = true;
        }

        if (moreRecent) {
            store.setClusterNodes(membershipMessage.getClusterNodes());
        }
    }
}
