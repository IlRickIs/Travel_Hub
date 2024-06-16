package it.unimib.travelhub.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;

import it.unimib.travelhub.R;
import it.unimib.travelhub.model.TravelSegment;
import it.unimib.travelhub.ui.travels.TravelActivity;

public class TravelSegmentTimeRecyclerAdapter extends RecyclerView.Adapter<TravelSegmentTimeRecyclerAdapter.ViewHolder>{

    List<TravelSegment> data;
    private static final int VIEW_TYPE_TOP = 0;
    private static final int VIEW_TYPE_MIDDLE = 1;
    private static final int VIEW_TYPE_BOTTOM = 2;


    public TravelSegmentTimeRecyclerAdapter(List<TravelSegment> data) {
        this.data = data;
    }
    @NonNull
    @Override
    public TravelSegmentTimeRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_timeline_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TravelSegmentTimeRecyclerAdapter.ViewHolder holder, final int position) {
        TravelSegment item = data.get(position);


        switch(holder.getItemViewType()) {
            case VIEW_TYPE_TOP:
                holder.seg_top_line.setVisibility(View.GONE);
                break;
            case VIEW_TYPE_MIDDLE:
                break;
            case VIEW_TYPE_BOTTOM:
                holder.seg_bottom_line.setVisibility(View.GONE);
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
        View seg_top_line;
        View seg_bottom_line;
        View seg_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            seg_location = itemView.findViewById(R.id.seg_location);
            seg_date_from = itemView.findViewById(R.id.seg_date_from);
            seg_date_to = itemView.findViewById(R.id.seg_date_to);
            seg_icon = itemView.findViewById(R.id.item_icon);
            seg_top_line = itemView.findViewById(R.id.seg_top_line);
            seg_bottom_line = itemView.findViewById(R.id.seg_bottom_line);
        }

        public void bind(TravelSegment travelSegment) {



            if (travelSegment.getDateFrom() != null && travelSegment.getDateTo() != null) {
                @SuppressLint("SimpleDateFormat") String dateFrom = new SimpleDateFormat("dd/MM/yyyy").format(travelSegment.getDateFrom());
                @SuppressLint("SimpleDateFormat") String dateTo = new SimpleDateFormat("dd/MM/yyyy").format(travelSegment.getDateTo());
                seg_date_from.setText(dateFrom);
                seg_date_to.setText(dateTo);


                long currentTime = System.currentTimeMillis();
                if (currentTime >= travelSegment.getDateFrom().getTime() && currentTime <= travelSegment.getDateTo().getTime()) {
                    seg_icon.setBackgroundResource(R.drawable.baseline_location_on_24);
                    //seg_icon.getBackground().setTint(itemView.getResources().getColor(R.color.orange));
                } else if (currentTime > travelSegment.getDateTo().getTime()) {
                    seg_icon.setBackgroundResource(R.drawable.baseline_location_on_24);
                    //seg_icon.getBackground().setTint(itemView.getResources().getColor(R.color.grey_separator));
                } else {
                    seg_icon.setBackgroundResource(R.drawable.baseline_location_on_24);
                    //seg_icon.getBackground().setTint(itemView.getResources().getColor(R.color.primaryVariantColor));
                }
            }

            TextView seg_description_text = itemView.findViewById(R.id.seg_description_text);
            LinearLayout seg_description = itemView.findViewById(R.id.seg_description);

            if (travelSegment.getDescription() == null || travelSegment.getDescription().isEmpty()) {
                seg_description_text.setText(R.string.travel_no_description);
            } else {
                seg_description_text.setText(travelSegment.getDescription());
            }

            TextView seg_details = itemView.findViewById(R.id.seg_details);

            seg_details.setOnClickListener(v -> {
                if (seg_description.getVisibility() == View.GONE) {
                    seg_description.setVisibility(View.VISIBLE);
                    seg_details.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_keyboard_arrow_up_24, 0);
                } else {
                    seg_description.setVisibility(View.GONE);
                    seg_details.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_keyboard_arrow_down_24, 0);
                }
            });


            // Manage the click of item in menu

            seg_location.setText(travelSegment.getLocation());
        }
    }


}
