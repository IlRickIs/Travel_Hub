package it.unimib.travelhub.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unimib.travelhub.R;
import it.unimib.travelhub.model.TravelMember;
import it.unimib.travelhub.ui.travels.TravelActivity;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.ViewHolder> {
    List<TravelMember> data;
    int type;
    String textcolor;

    boolean isTravelActivity;

    public interface OnLongButtonClickListener {
        void onLongButtonItemClick(TravelMember travelMember, ImageView seg_long_button);
    }
    private static OnLongButtonClickListener onLongButtonClickListener = null;

    public UsersRecyclerAdapter(List<TravelMember> data, int type, boolean isTravelActivity, String textcolor, OnLongButtonClickListener onLongButtonClickListener) {
        UsersRecyclerAdapter.onLongButtonClickListener = onLongButtonClickListener;
        this.isTravelActivity = isTravelActivity;
        this.data = data;
        this.type = type;
        this.textcolor = textcolor;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if (type == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.compact_friend_list_item, parent, false);
        }
        return new ViewHolder(view, type, isTravelActivity);
    }
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(data.get(position), textcolor);
    }
    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView participant_name;
        ImageView participant_image;
        ImageView participant_creator;
        int type;

        boolean isTravelActivity;
        public ViewHolder(@NonNull View itemView, int type, boolean isTravelActivity) {
            super(itemView);
            this.type = type;
            this.isTravelActivity = isTravelActivity;
            participant_name = itemView.findViewById(R.id.participant_name);
            participant_image = itemView.findViewById(R.id.participant_image);
            participant_creator = itemView.findViewById(R.id.participant_creator_flag);
        }
        public void bind(TravelMember travelMember, String textcolor) {
            participant_name.setText(travelMember.getUsername());
            participant_name.setTextColor(Color.parseColor(textcolor));

            if (travelMember.getRole() == TravelMember.Role.CREATOR) {
                participant_creator.setVisibility(View.VISIBLE);
            }

            if (isTravelActivity){
                participant_image.setOnLongClickListener(v -> {
                    Log.d("UsersRecyclerAdapter", "onLongClick: " + travelMember.getUsername());
                    if (((TravelActivity) itemView.getContext()).isTravelCreator && ((TravelActivity) itemView.getContext()).enableEdit){
                        onLongButtonClickListener.onLongButtonItemClick(travelMember, participant_image);
                    }
                    return true;
                });
            }


        }
    }

}