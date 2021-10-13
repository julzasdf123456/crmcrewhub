package com.lopez.julz.crmcrewhub.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lopez.julz.crmcrewhub.R;

import java.util.List;

public class BarangaysAdapter extends RecyclerView.Adapter<BarangaysAdapter.ViewHolder> {

    public List<Barangays> barangaysList;
    public Context context;

    public BarangaysAdapter(List<Barangays> barangaysList, Context context) {
        this.barangaysList = barangaysList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.barangays_recyclerview_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Barangays barangays = barangaysList.get(position);

        holder.barangaysName.setText(barangays.getBarangay());
    }

    @Override
    public int getItemCount() {
        return barangaysList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView barangaysName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            barangaysName = itemView.findViewById(R.id.barangayName);
        }
    }
}
