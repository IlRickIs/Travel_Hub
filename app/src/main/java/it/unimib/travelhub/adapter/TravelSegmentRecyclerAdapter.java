package it.unimib.travelhub.adapter;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import it.unimib.travelhub.R;
import it.unimib.travelhub.model.TravelSegment;

public class TravelSegmentRecyclerAdapter extends RecyclerView.Adapter<TravelSegmentRecyclerAdapter.ViewHolder> {

    List<TravelSegment> data;
    private static final int VIEW_TYPE_TOP = 0;
    private static final int VIEW_TYPE_MIDDLE = 1;
    private static final int VIEW_TYPE_BOTTOM = 2;


    public TravelSegmentRecyclerAdapter(List<TravelSegment> data) {
        this.data = data;
    }
    @NonNull
    @Override
    public TravelSegmentRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_timeline_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TravelSegmentRecyclerAdapter.ViewHolder holder, final int position) {
        TravelSegment item = data.get(position);
         //Populate views...


        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.seg_line.getLayoutParams();

        switch(holder.getItemViewType()) {
            case VIEW_TYPE_TOP:
                // The top of the line has to be rounded
                Drawable top_rounded = holder.seg_line.getResources().getDrawable(R.drawable.line_bg_top);
                holder.seg_line.setBackground(top_rounded);
                break;
            case VIEW_TYPE_MIDDLE:
                // Only the color could be enough
                // but a drawable can be used to make the cap rounded also here
                Drawable middle_rounded = holder.seg_line.getResources().getDrawable(R.drawable.line_bg_middle);
                params.setMargins(0, 0, 0, 0);
                holder.seg_line.setBackground(middle_rounded);
                break;
            case VIEW_TYPE_BOTTOM:
                Drawable bottom_rounded = holder.seg_line.getResources().getDrawable(R.drawable.line_bg_bottom);
                holder.seg_line.setBackground(bottom_rounded);
                params.setMargins(0, 0, 0, 0);
                break;
        }
        holder.bind(item);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return VIEW_TYPE_TOP;}
        else if(position == data.size() - 1) {
                return VIEW_TYPE_BOTTOM;
            }
            return VIEW_TYPE_MIDDLE;
        }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView seg_location;
        TextView seg_date_from;
        TextView seg_date_to;

        TextView seg_description;

        ConstraintLayout seg_line;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            seg_location = itemView.findViewById(R.id.seg_location);
            seg_date_from = itemView.findViewById(R.id.seg_date_from);
            seg_date_to = itemView.findViewById(R.id.seg_date_to);
            seg_line = itemView.findViewById(R.id.item_line);
            seg_description = itemView.findViewById(R.id.seg_description);
        }

        public void bind(TravelSegment travelSegment) {

            @SuppressLint("SimpleDateFormat") String dateFrom = new SimpleDateFormat("dd/MM/yyyy").format(travelSegment.getDateFrom());
            @SuppressLint("SimpleDateFormat") String dateTo = new SimpleDateFormat("dd/MM/yyyy").format(travelSegment.getDateTo());

            seg_location.setText(travelSegment.getLocation());
            seg_date_from.setText(dateFrom);
            seg_date_to.setText(dateTo);
            seg_description.setText(travelSegment.getDescription());
        }
    }
}
