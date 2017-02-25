package io.lattis.hitme.model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ChatNode extends RealmObject {

    private String contact;
    private RealmList<ChatBox> chatBox;

    public ChatNode( String contact, RealmList<ChatBox> chatBox) {
        this.contact = contact;
        this.chatBox = chatBox;
    }

    public ChatNode() {
    }


    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public RealmList<ChatBox> getChatBox() {
        return chatBox;
    }

    public void setChatBox(RealmList<ChatBox> chatBox) {
        this.chatBox = chatBox;
    }
}