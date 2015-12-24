package razrsword.dining;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import razrsword.main.R;

/**
 * Created by razrs on 23-Dec-15.
 */

public class DiningViewAdapter extends RecyclerView.Adapter<DiningViewAdapter.locationViewHolder>
        implements ItemTouchHelperAdapter {

    List<DiningLocation> diningLocations;

    @Override
    public locationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dining_card_layout, parent, false);
        locationViewHolder lvh = new locationViewHolder(v);
        return lvh;
    }

    DiningViewAdapter(List<DiningLocation> diningLocations) {
        this.diningLocations = diningLocations;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(locationViewHolder locationViewHolder, int i) {
        locationViewHolder.locationName.setText(diningLocations.get(i).locationName);
        locationViewHolder.locationImage.setImageResource(diningLocations.get(i).imageID);
    }

    @Override
    public int getItemCount() {
        return diningLocations.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(diningLocations, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(diningLocations, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    public static class locationViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView locationName;
        ImageView locationImage;

        locationViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.location_card);
            locationName = (TextView) itemView.findViewById(R.id.location_name);
            locationImage = (ImageView) itemView.findViewById(R.id.dining_location_image);
        }
    }

}