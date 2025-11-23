package com.example.mineralstore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mineralstore.model.Mineral;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MineralAdapter extends RecyclerView.Adapter<MineralAdapter.MineralViewHolder> {

    private List<Mineral> mineralList = new ArrayList<>();

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
        holder.tvPrice.setText(String.format(Locale.getDefault(), "%d ₽", mineral.getPrice()));

        // Загрузка изображения
        if (mineral.getImageRes() != null && !mineral.getImageRes().isEmpty()) {
            String fileName = mineral.getImageRes()
                    .replace(".jpg", "")
                    .replace(".png", "")
                    .replace(".jpeg", "");

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

        // Кнопка "В корзину"
        holder.btnAddToCart.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(mineral);

            holder.btnAddToCart.setText("Добавлено!");
            holder.btnAddToCart.setIconResource(R.drawable.ic_check);
            holder.btnAddToCart.setEnabled(false);

            holder.itemView.postDelayed(() -> {
                holder.btnAddToCart.setText("В корзину");
                holder.btnAddToCart.setIconResource(R.drawable.ic_shopping_cart);
                holder.btnAddToCart.setEnabled(true);
            }, 1200);

            if (HomeActivity.instance != null) {
                HomeActivity.instance.updateCartCounter();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mineralList.size();
    }

    // Это и есть твой ViewHolder — он должен быть здесь!
    static class MineralViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMineral;
        TextView tvName;
        TextView tvPrice;
        MaterialButton btnAddToCart;

        public MineralViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMineral = itemView.findViewById(R.id.ivMineral);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}