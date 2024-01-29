package it.unimib.travelhub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.unimib.travelhub.R;

public class AddRecyclerAdapter extends RecyclerView.Adapter<AddRecyclerAdapter.ViewHolder>{

    private String[] myDataset = {"Add Activity", "Add Accomodation", "Add Restaurant", "Add Transport"};
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Button button;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            button = (Button) view.findViewById(R.id.button_add_activity);
        }

        public Button getButton() {
            return button;
        }
    }

    public AddRecyclerAdapter(String[] myDataset) {
        this.myDataset = myDataset;
    }
    @NonNull
    @Override
    public AddRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.edit_activities_row, parent, false);

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
}
