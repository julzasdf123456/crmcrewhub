package com.lopez.julz.crmcrewhub.classes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.lopez.julz.crmcrewhub.BarangaysActivity;
import com.lopez.julz.crmcrewhub.R;

import java.util.List;

public class TownsAdapter extends RecyclerView.Adapter<TownsAdapter.ViewHolder> {

    public List<Towns> townsList;
    public Context context;

    public TownsAdapter(List<Towns> townsList, Context context) {
        this.townsList = townsList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.towns_recyclerview_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Towns town = townsList.get(position);

        holder.townName.setText("(" + town.getId() + ") " + town.getTown());
        if (town.getDistrict() != null) {
            holder.district.setText("District " + town.getDistrict());
        } else {
            holder.district.setText("No district specified");
        }

        holder.townParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BarangaysActivity.class);
                intent.putExtra("ID", town.getId());
                intent.putExtra("TOWN", town.getTown());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return townsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView townName, district;
        public CoordinatorLayout townParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            townName = itemView.findViewById(R.id.townName);
            district = itemView.findViewById(R.id.district);
            townParent = itemView.findViewById(R.id.townParent);
        }
    }
}
