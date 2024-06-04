package it.unimib.travelhub.adapter;

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

    public UsersRecyclerAdapter(List<TravelMember> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_item, parent, false);
        return new ViewHolder(view);
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
        TextView friend_name;

        ImageView friend_image;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            friend_name = itemView.findViewById(R.id.friend_name);
            friend_image = itemView.findViewById(R.id.friend_image);
        }

        public void bind(TravelMember travelMember) {
            friend_name.setText(travelMember.getUsername());
        }
    }

}