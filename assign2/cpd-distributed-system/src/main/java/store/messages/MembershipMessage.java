package store.messages;

import store.Utils;

public class MembershipMessage extends Message {
    public MembershipMessage(char[] id) {
        super("header " + Utils.keyToString(id), null, id, 0);
    }
}
