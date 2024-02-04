package it.unimib.travelhub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import it.unimib.travelhub.R;

public class AddRecyclerAdapter extends RecyclerView.Adapter<AddRecyclerAdapter.ViewHolder>{

    private String[] myDataset;

    private final OnItemClickListener onItemClickListener;

    public String[] getDataSet() {
        return myDataset;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public AddRecyclerAdapter(String[] myDataset, OnItemClickListener onItemClickListener) {
        this.myDataset = myDataset;
        this.onItemClickListener = onItemClickListener;
    }

    public AddRecyclerAdapter(String[] myDataset) {
        this.myDataset = myDataset;
        onItemClickListener = null;
    }

    public void setDataSet(String[] myDataset) {
        this.myDataset = myDataset;
    }
    @NonNull
    @Override
    public AddRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.travel_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddRecyclerAdapter.ViewHolder holder, int position) {
        holder.getButton().setText(myDataset[position]);
    }

    @Override
    public int getItemCount() {
        return myDataset.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Button button;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the DestinationsViewHolder's View

            button = (Button) view.findViewById(R.id.button_add_activity);
            button.setOnClickListener(this);
        }

        public Button getButton() {
            return button;
        }

        @Override
        public void onClick(View v) {
            // Triggers click upwards to the adapter on click
            if (onItemClickListener != null && v.getId() == R.id.button_add_activity) {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(position);
                    notifyItemChanged(position);
                }
            }
        }
    }


}
