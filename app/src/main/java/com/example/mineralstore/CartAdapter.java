package com.example.mineralstore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mineralstore.model.CartItem;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> items = new ArrayList<>();
    private final Runnable onUpdate;

    public CartAdapter(List<CartItem> items, Runnable onUpdate) {
        this.items = new ArrayList<>(items);
        this.onUpdate = onUpdate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);

        // Картинка (уже есть, оставляем)
        if (item.mineral != null && item.mineral.getImageRes() != null && !item.mineral.getImageRes().isEmpty()) {
            String fileName = item.mineral.getImageRes()
                    .replace(".jpg", "").replace(".png", "").replace(".jpeg", "");
            int resId = holder.itemView.getContext().getResources()
                    .getIdentifier(fileName, "drawable", holder.itemView.getContext().getPackageName());
            holder.ivMineral.setImageResource(resId != 0 ? resId : R.drawable.ic_mineral_placeholder);
        } else {
            holder.ivMineral.setImageResource(R.drawable.ic_mineral_placeholder);
        }

        holder.tvName.setText(item.mineral.getName());
        holder.tvPrice.setText(String.format("%,d ₽", item.mineral.getPrice() * item.quantity));
        holder.tvQuantity.setText(String.valueOf(item.quantity));

        // КНОПКИ — ГЛАВНОЕ ИСПРАВЛЕНИЕ!
        holder.btnMinus.setOnClickListener(v -> {
            CartManager.getInstance().updateQuantity(item, item.quantity - 1);
            notifyItemChanged(position);     // обновляем строку
            onUpdate.run();                  // ← ЭТО ОБНОВЛЯЕТ ОБЩУЮ СУММУ!
        });

        holder.btnPlus.setOnClickListener(v -> {
            CartManager.getInstance().updateQuantity(item, item.quantity + 1);
            notifyItemChanged(position);
            onUpdate.run();                  // ← ЭТО ВАЖНО!
        });

        holder.btnDelete.setOnClickListener(v -> {
            CartManager.getInstance().removeFromCart(item);
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                items.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, items.size());
                onUpdate.run();                  // ← и тут тоже!
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity;
        ImageView ivMineral;
        MaterialButton btnMinus, btnPlus, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            ivMineral = itemView.findViewById(R.id.ivMineral);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
    public void updateList(List<CartItem> newList) {
        items = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
}