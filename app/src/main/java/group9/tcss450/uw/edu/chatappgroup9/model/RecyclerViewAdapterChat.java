package group9.tcss450.uw.edu.chatappgroup9.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import group9.tcss450.uw.edu.chatappgroup9.R;

public class RecyclerViewAdapterChat extends RecyclerView.Adapter<RecyclerViewAdapterChat.ViewHolder> {

    private List<String> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView myProfileImage;
        public ImageView myOtherProfileImage;
        public TextView myMessage;
        public TextView myOtherUserMessage;
        public ViewHolder(View itemView) {
            super(itemView);
            myProfileImage = itemView.findViewById(R.id.recycleViewItemProfileImage);
            myMessage = itemView.findViewById(R.id.recyclyViewItemUserMessages);
            myOtherProfileImage = itemView.findViewById(R.id.recycleViewOtherUserProfileImage);
            myOtherUserMessage = itemView.findViewById(R.id.recyCleViewItemOtherUserMessage);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapterChat(List<String> theDataset) {
        mDataset = theDataset;
    }



    public void addData(String newMessage) {
        mDataset.add(newMessage);
        notifyDataSetChanged();
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapterChat.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_view_item_chat, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String[] data = mDataset.get(position).split(":");

        if (data.length > 0) {

            if (data[0].equals(data[1])) {
                holder.myMessage.setText(data[2]);
            }
            if (!data[0].equals(data[1])) {
                holder.myOtherProfileImage.setVisibility(View.VISIBLE);
                holder.myOtherUserMessage.setVisibility(View.VISIBLE);
                holder.myOtherUserMessage.setText(data[2]);
                holder.myMessage.setVisibility(View.INVISIBLE);
                holder.myProfileImage.setVisibility(View.INVISIBLE);
            }


        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
