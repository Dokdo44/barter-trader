package com.example.a3130_grp8_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.model.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, h:mm a");

    private Context context;
    List<Message> messages;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView senderTextView;
        private final TextView messageTextView;
        private final TextView timeTextView;
        public ViewHolder(View view) {
            super(view);

            senderTextView = view.findViewById(R.id.senderTextView);
            messageTextView = view.findViewById(R.id.messageTextView);
            timeTextView = view.findViewById(R.id.timeTextView);
        }

        public TextView getSenderTextView() {
            return senderTextView;
        }

        public TextView getMessageTextView() {
            return messageTextView;
        }

        public TextView getTimeTextView() {
            return timeTextView;
        }
    }

    public MessagesAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_message, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() { return messages.size(); }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Message currMessage = messages.get(position);
        viewHolder.getSenderTextView().setText(currMessage.getSenderName());
        viewHolder.getMessageTextView().setText(currMessage.getMessageString());
        viewHolder.getTimeTextView().setText(formatTimeSent(currMessage.getTimeSent()));
    }

    private String formatTimeSent(String timeSent) {
        if (timeSent == null) {
            return "";
        }
        try {
            return LocalDateTime.parse(timeSent).format(TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return timeSent;
        }
    }

    public void addMessage(int position, Message message) {
        if(!messages.contains(message)) {
            messages.add(position, message);
        }
        notifyItemInserted(position);
    }

    public List<Message> getMessages() {
        return messages;
    }
}