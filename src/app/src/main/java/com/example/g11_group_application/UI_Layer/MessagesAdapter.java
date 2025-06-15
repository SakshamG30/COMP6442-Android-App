package com.example.g11_group_application.UI_Layer;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.g11_group_application.R;
import com.example.g11_group_application.Service_layer.Message;

import java.util.List;

/**
 * @Author: Saksham Gupta (u7726995)
 * @Editted: Aditya Iyengar (u7670692)
 * Created: 14-May-2024
 * Adapter for displaying messages in a RecyclerView.
 * Handles binding message data to the view holder.
 *
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private final List<Message> messages;

    private final String currentUserName;

    public MessagesAdapter(List<Message> messages, String currentUserName) {
        this.messages = messages;
        this.currentUserName = currentUserName;
    }

    /**
     * Creates a new view holder when there are no existing view holders that the RecyclerView can reuse.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new MessageViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_user, parent, false);
        return new MessageViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the ViewHolder to reflect the item at the given position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        String styledText = String.format("<b>%s</b><br>%s<br><i>%s,  %s</i>",
                message.getUserName(), message.getMessage(), message.getTime(), message.getDate());

        holder.messageText.setText(HtmlCompat.fromHtml(styledText, HtmlCompat.FROM_HTML_MODE_LEGACY));

        // Align message to the right if it is from the current user, otherwise align to the left
        if (message.getUserName().equalsIgnoreCase(currentUserName)) {
            holder.messageText.setGravity(Gravity.RIGHT);
            holder.messageText.setBackgroundResource(R.drawable.message_background_user);
        } else {
            holder.messageText.setGravity(Gravity.LEFT);
            holder.messageText.setBackgroundResource(R.drawable.message_background_user);
        }

        // Create spacing between each message
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        layoutParams.bottomMargin = 24; // 24px bottom margin
        holder.itemView.setLayoutParams(layoutParams);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * ViewHolder class for holding message views.
     */
    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
        }
    }
}
