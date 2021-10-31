
package com.rajendra.onlinedailygroceries.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rajendra.onlinedailygroceries.R;
import com.rajendra.onlinedailygroceries.eventbus.MyUpdateCartEvent;
import com.rajendra.onlinedailygroceries.listener.ICartLoadListener;
import com.rajendra.onlinedailygroceries.listener.IRecyclerViewClickListener;
import com.rajendra.onlinedailygroceries.model.CartModel;
import com.rajendra.onlinedailygroceries.model.ItemsModel;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyItemAdapter extends RecyclerView.Adapter<MyItemAdapter.MyItemViewHolder> {

    private Context context;
    private List<ItemsModel> itemsModelList;
    private ICartLoadListener iCartLoadListener;

    public MyItemAdapter(Context context, List<ItemsModel> itemsModelList, ICartLoadListener iCartLoadListener) {
        this.context = context;
        this.itemsModelList = itemsModelList;
        this.iCartLoadListener = iCartLoadListener;
    }

    @NonNull
    @Override
    public MyItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyItemViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyItemViewHolder holder, int position) {
        Glide.with(context)
                .load(itemsModelList.get(position).getImage())
                .into(holder.imageView);
        holder.txtPrice.setText( new StringBuilder("$").append(itemsModelList.get(position).getPrice()));
        holder.txtName.setText(new StringBuilder().append(itemsModelList.get(position).getName()));

        holder.setListener((view, posision) -> {
            addToCart(itemsModelList.get(posision));
        });

    }

    private void addToCart(ItemsModel itemsModel) {
        DatabaseReference userCart = FirebaseDatabase
                .getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID");
        userCart.child(itemsModel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            CartModel cartModel = snapshot.getValue(CartModel.class);
                            cartModel.setQuantity(cartModel.getQuantity()+1);
                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("quantity", cartModel.getQuantity());
                            updateData.put("totalPrice", cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));

                            userCart.child(itemsModel.getKey())
                                    .updateChildren(updateData)
                                    .addOnSuccessListener(aVoid -> {
                                        iCartLoadListener.onCartLoadFailed("Add to Cart Sucsses");
                                    }).addOnFailureListener(e -> iCartLoadListener.onCartLoadFailed(e.getMessage()));

                        }
                        else {
                            CartModel cartModel = new CartModel();
                            cartModel.setName(itemsModel.getName());
                            cartModel.setImage(itemsModel.getImage());
                            cartModel.setKey(itemsModel.getKey());
                            cartModel.setPrice(itemsModel.getPrice());
                            cartModel.setQuantity(1);
                            cartModel.setTotalPrice(Float.parseFloat(itemsModel.getPrice()));

                            userCart.child(itemsModel.getKey())
                                    .setValue(cartModel)
                                    .addOnSuccessListener(aVoid -> {
                                iCartLoadListener.onCartLoadFailed("Add to Cart Sucsses");
                            }).addOnFailureListener((OnFailureListener) e -> iCartLoadListener.onCartLoadFailed(e.getMessage()));
                        }

                        EventBus.getDefault().postSticky(new MyUpdateCartEvent());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        iCartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return itemsModelList.size();
    }

    public class MyItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtPrice)
        TextView txtPrice;

        IRecyclerViewClickListener listener;

        public void setListener(IRecyclerViewClickListener listener) {
            this.listener = listener;
        }

        private Unbinder unbinder;
        public MyItemViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onRycyclerClick(view, getAdapterPosition());
        }
    }
}
