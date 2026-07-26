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
import com.example.a3130_grp8_app.activities.ViewRequestsActivity;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.User;

import java.util.List;

public class ChatsListItemAdapter extends RecyclerView.Adapter<ChatsListItemAdapter.ViewHolder> {

    private List<Chat> chats;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView providerNameTextView;
        private final TextView receiverNameTestView;
        private final TextView productNameTextView;
        private final ConstraintLayout chatListItemMain;

        public ViewHolder(View view) {
            super(view);

            productNameTextView = view.findViewById(R.id.productNameTextView);
            providerNameTextView = view.findViewById(R.id.providerNameTetView);
            receiverNameTestView = view.findViewById(R.id.receiverTextView);
            chatListItemMain = view.findViewById(R.id.chatListItemMain);
        }

        public TextView getProductNameTextView() {
            return productNameTextView;
        }
        public TextView getProviderNameTextView() { return providerNameTextView; }

        public ConstraintLayout getChatListItemMain() {
            return chatListItemMain;
        }

        public TextView getReceiverNameTestView() {
            return receiverNameTestView;
        }
    }

    public ChatsListItemAdapter(Context context, List<Chat> chats) {
        this.chats = chats;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_chats_list, viewGroup, false);


        return new ViewHolder(view);
    }

    private View.OnClickListener getOnProductClickListener(Chat chat) {
        return view -> {
            Intent intent = new Intent(context, ProductChatActivity.class);
            intent.putExtra("CHAT", chat);
            context.startActivity(intent);
        };
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Chat chat = chats.get(position);
        Product chatProduct = chat.getProduct();
        viewHolder.getProductNameTextView().setText(chatProduct.getName());

        User provider = chat.getProvider();
        User receiver = chat.getReceiver();
        viewHolder.getProviderNameTextView().setText(provider.getEmail());
        viewHolder.getReceiverNameTestView().setText(receiver.getEmail());

        viewHolder.getChatListItemMain().setOnClickListener(getOnProductClickListener(chat));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void addChat(int position, Chat chat) {
        chats.add(position, chat);
        notifyItemInserted(position);
    }

}
