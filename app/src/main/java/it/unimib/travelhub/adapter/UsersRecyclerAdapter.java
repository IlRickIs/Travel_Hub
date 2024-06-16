package it.unimib.travelhub.adapter;

import android.app.Activity;
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
import it.unimib.travelhub.ui.travels.AddTravelActivity;
import it.unimib.travelhub.ui.travels.TravelActivity;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.ViewHolder> {
    List<TravelMember> data;
    int type;

    Activity activity;

    public interface OnLongButtonClickListener {
        void onLongButtonItemClick(TravelMember travelMember, ImageView seg_long_button);
    }
    private static OnLongButtonClickListener onLongButtonClickListener = null;

    public UsersRecyclerAdapter(List<TravelMember> data, int type, Activity activity, OnLongButtonClickListener onLongButtonClickListener) {
        UsersRecyclerAdapter.onLongButtonClickListener = onLongButtonClickListener;
        this.activity = activity;
        this.data = data;
        this.type = type;
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
        return new ViewHolder(view, type, activity);
    }
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(data.get(position));
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

        Activity activity;
        public ViewHolder(@NonNull View itemView, int type, Activity activity) {
            super(itemView);
            this.type = type;
            this.activity = activity;
            participant_name = itemView.findViewById(R.id.participant_name);
            participant_image = itemView.findViewById(R.id.participant_image);
            participant_creator = itemView.findViewById(R.id.participant_creator_flag);
        }
        public void bind(TravelMember travelMember) {
            participant_name.setText(travelMember.getUsername());

            if (travelMember.getRole() == TravelMember.Role.CREATOR) {
                participant_creator.setVisibility(View.VISIBLE);
            }

            if (activity instanceof TravelActivity){
                participant_image.setOnLongClickListener(v -> {
                    if (((TravelActivity) itemView.getContext()).isTravelCreator && ((TravelActivity) itemView.getContext()).enableEdit){
                        onLongButtonClickListener.onLongButtonItemClick(travelMember, participant_image);
                    }
                    return true;
                });
            } else if (activity instanceof AddTravelActivity){
                participant_image.setOnLongClickListener(v -> {
                    onLongButtonClickListener.onLongButtonItemClick(travelMember, participant_image);
                    return true;
                });

            }


        }
    }

}