package it.unimib.travelhub.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import it.unimib.travelhub.R;
import it.unimib.travelhub.model.Travels;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder>{

    /**
     * Interface to associate a click listener with
     * a RecyclerView item.
     */
    public interface OnItemClickListener {
        void onTravelsItemClick(Travels travels);
    }

    private final List<Travels> travelsList;
    private final OnItemClickListener onItemClickListener;

    public HomeRecyclerAdapter(List<Travels> travelsList, OnItemClickListener onItemClickListener) {
        this.travelsList = travelsList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.home_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(travelsList.get(position));
    }

    @Override
    public int getItemCount() {
        if (travelsList != null) {
            return travelsList.size();
        }
        return 0;
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView textViewTitle;
        private final TextView textViewStartDate;
        private final TextView textViewEndDate;
        private final TextView textViewDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.home_list_item_title);
            textViewStartDate = itemView.findViewById(R.id.home_list_item_start_date);
            textViewEndDate = itemView.findViewById(R.id.home_list_item_end_date);
            textViewDescription = itemView.findViewById(R.id.home_list_item_description);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SimpleDateFormat")
        public void bind(Travels travels) {
            textViewTitle.setText(travels.getTitle());
            textViewStartDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(travels.getStartDate()));
            textViewEndDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(travels.getEndDate()));
            textViewDescription.setText(travels.getDescription());
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onTravelsItemClick(travelsList.get(getBindingAdapterPosition()));
        }
    }
}
