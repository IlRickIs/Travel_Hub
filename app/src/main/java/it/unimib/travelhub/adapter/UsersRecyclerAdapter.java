package it.unimib.travelhub.adapter;

import android.graphics.Color;
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
import it.unimib.travelhub.model.User;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.ViewHolder> {
    List<TravelMember> data;
    int type;
    String textcolor;

    public UsersRecyclerAdapter(List<TravelMember> data, int type, String textcolor) {
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
        return new ViewHolder(view, type);
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
        TextView friend_name;
        ImageView friend_image;
        int type;
        public ViewHolder(@NonNull View itemView, int type) {
            super(itemView);
            this.type = type;
            friend_name = itemView.findViewById(R.id.friend_name);
            friend_image = itemView.findViewById(R.id.friend_image);
        }
        public void bind(TravelMember travelMember, String textcolor) {
            friend_name.setText(travelMember.getUsername());
            friend_name.setTextColor(Color.parseColor(textcolor));
        }
    }

}