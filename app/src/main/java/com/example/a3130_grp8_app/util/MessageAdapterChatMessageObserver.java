package com.example.a3130_grp8_app.util;

import com.example.a3130_grp8_app.adapters.MessagesAdapter;
import com.example.a3130_grp8_app.data.MessageStorage;
import com.example.a3130_grp8_app.model.Message;

import java.time.LocalDateTime;
import java.util.List;

public class MessageAdapterChatMessageObserver implements IMessageObserver {

    MessageNotifier messageNotifier;
    MessageStorage messageStorage;
    MessagesAdapter messagesAdapter;

    public MessageAdapterChatMessageObserver(MessageNotifier messageNotifier, MessageStorage MessageStorage, MessagesAdapter messagesAdapter) {
        this.messageNotifier = messageNotifier;
        this.messageStorage = MessageStorage;
        this.messagesAdapter = messagesAdapter;
    }

    /**
     * On a message update, the message is sorted into the adapter's messages in reverse
     * chronological order
     * @param update the message update
     */
    @Override
    public void update(Message update) {
        // Adds the message in reverse chronological order to adapter messages
        List<Message> messagesAdapterMessages = messagesAdapter.getMessages();
        int i = 0;
        for(; i < messagesAdapterMessages.size(); i++) {
            if(LocalDateTime.parse(update.getTimeSent()).isAfter(LocalDateTime.parse(messagesAdapterMessages.get(i).getTimeSent()))) {
                break;
            }
        }
        messagesAdapter.addMessage(i, update);
    }

    public MessagesAdapter getMessagesAdapter() {
        return messagesAdapter;
    }

    public void attachToMessageNotifier() {
        messageStorage.detach(this);
        messageNotifier.attach(this);
    }

    public void attachToMessageStorage() {
        messageNotifier.detach(this);
        messageStorage.attach(this);
    }
}
