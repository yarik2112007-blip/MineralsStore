package com.example.mineralstore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mineralstore.R;
import com.example.mineralstore.model.Mineral;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MineralAdapter extends RecyclerView.Adapter<MineralAdapter.MineralViewHolder> {

    private List<Mineral> mineralList = new ArrayList<>();

    // Обновление данных
    public void submitList(List<Mineral> newList) {
        mineralList.clear();
        if (newList != null) {
            mineralList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MineralViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mineral, parent, false);
        return new MineralViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MineralViewHolder holder, int position) {
        Mineral mineral = mineralList.get(position);
        holder.tvName.setText(mineral.getName());

        // Форматируем цену: 129000 → 1 290 ₽
        String priceText = String.format(Locale.getDefault(), "%,d ₽", mineral.getPrice());
        holder.tvPrice.setText(priceText);

        // Загрузка изображения — пока просто из drawable по имени
        if (mineral.getImageRes() != null && !mineral.getImageRes().isEmpty()) {
            String fileName = mineral.getImageRes().replace(".jpg", "").replace(".png", "");
            int resId = holder.itemView.getContext().getResources()
                    .getIdentifier(fileName, "drawable", holder.itemView.getContext().getPackageName());
            if (resId != 0) {
                holder.ivMineral.setImageResource(resId);
            } else {
                holder.ivMineral.setImageResource(R.drawable.ic_mineral_placeholder);
            }
        } else {
            holder.ivMineral.setImageResource(R.drawable.ic_mineral_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return mineralList.size();
    }

    static class MineralViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMineral;
        TextView tvName;
        TextView tvPrice;

        public MineralViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMineral = itemView.findViewById(R.id.ivMineral);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}