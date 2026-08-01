package com.example.a3130_grp8_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.activities.ProductChatActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Chat;

import java.util.List;

public class ChatsListItemAdapter extends RecyclerView.Adapter<ChatsListItemAdapter.ViewHolder> {

    private List<Chat> chats;
    private Context context;

    /**
     * The viewholder for chat items
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView avatarInitialTextView;
        private final TextView otherPersonNameTextView;
        private final TextView productNameTextView;
        private final ConstraintLayout chatListItemMain;

        public ViewHolder(View view) {
            super(view);

            avatarInitialTextView = view.findViewById(R.id.avatarInitialTextView);
            otherPersonNameTextView = view.findViewById(R.id.otherPersonNameTextView);
            productNameTextView = view.findViewById(R.id.productNameTextView);
            chatListItemMain = view.findViewById(R.id.chatListItemMain);
        }

        public TextView getAvatarInitialTextView() {
            return avatarInitialTextView;
        }

        public TextView getOtherPersonNameTextView() {
            return otherPersonNameTextView;
        }

        public TextView getProductNameTextView() {
            return productNameTextView;
        }

        public ConstraintLayout getChatListItemMain() {
            return chatListItemMain;
        }
    }

    /**
     * Constructs the adapter
     * @param context the application context that the adapter is in
     * @param chats the chat items to add to the view
     */
    public ChatsListItemAdapter(Context context, List<Chat> chats) {
        this.chats = chats;
        this.context = context;
    }

    /**
     * Creaes the ViewHolder
     * @param viewGroup   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return the created view in the ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_chat_list_item, viewGroup, false);


        return new ViewHolder(view);
    }

    private View.OnClickListener getOnProductClickListener(Chat chat) {
        return view -> {
            Intent intent = new Intent(context, ProductChatActivity.class);
            intent.putExtra("CHAT", chat);
            context.startActivity(intent);
        };
    }

    /**
     * Sets the text of the view, showing the identity of whichever party
     * is NOT the current user (the "other side" of the chat).
     * @param viewHolder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Chat chat = chats.get(position);

        String currentUserId = FirebaseManager.getInstance().getCurrentUserID();
        boolean currentUserIsProvider = currentUserId != null && currentUserId.equals(chat.getProviderUid());
        String otherPersonName = currentUserIsProvider ? chat.getReceiverName() : chat.getProviderName();

        viewHolder.getOtherPersonNameTextView().setText(otherPersonName);
        viewHolder.getProductNameTextView().setText(chat.getProductName());

        String initial = (otherPersonName != null && !otherPersonName.isEmpty())
                ? otherPersonName.substring(0, 1).toUpperCase()
                : "?";
        viewHolder.getAvatarInitialTextView().setText(initial);

        viewHolder.getChatListItemMain().setOnClickListener(getOnProductClickListener(chat));
    }

    /**
     * gets the number of chats in the adapter
     * @return the number of Chat items in the adapter
     */
    @Override
    public int getItemCount() {
        return chats.size();
    }

    /**
     * Add a Chat item to the adapter
     * @param position the position that the chat should be inserted into in the adapter
     * @param chat the Chat item
     */
    public void addChat(int position, Chat chat) {
        if(!chats.contains(chat)) {
            chats.add(position, chat);
            notifyItemInserted(position);
        }
    }

}