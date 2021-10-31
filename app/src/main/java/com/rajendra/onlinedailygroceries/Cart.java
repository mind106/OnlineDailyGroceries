
package com.rajendra.onlinedailygroceries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;
import com.rajendra.onlinedailygroceries.adapter.MyItemAdapter;
import com.rajendra.onlinedailygroceries.eventbus.MyUpdateCartEvent;
import com.rajendra.onlinedailygroceries.listener.ICartLoadListener;
import com.rajendra.onlinedailygroceries.listener.IDItemLoadListener;
import com.rajendra.onlinedailygroceries.model.CartModel;
import com.rajendra.onlinedailygroceries.model.ItemsModel;
import com.rajendra.onlinedailygroceries.utils.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Cart extends AppCompatActivity implements IDItemLoadListener, ICartLoadListener {


    @BindView(R.id.recycler_drink)
    RecyclerView recycler_dink;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.badge)
    NotificationBadge badge;
    @BindView(R.id.btnCart)
    FrameLayout btnCart;

    IDItemLoadListener itemLoadListener;
    ICartLoadListener cartLoadListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        init();
        LoadItemsFromFirebase();
        countCartItem();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if(EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class)){
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        }

        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUpdateCart(MyUpdateCartEvent myUpdateCartEvent){
        countCartItem();

    }

    private void LoadItemsFromFirebase() {
        List<ItemsModel> itemsModels = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Drink")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot itemSnapshot:snapshot.getChildren())
                            {
                                ItemsModel itemModel = itemSnapshot.getValue(ItemsModel.class);
                                itemModel.setKey(itemSnapshot.getKey());
                                itemsModels.add(itemModel);
                            }
                            itemLoadListener.onItemLoadSuccess(itemsModels);

                        }
                        else {
                            itemLoadListener.onItemLoadFailed("Can't find Item");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        itemLoadListener.onItemLoadFailed(error.getMessage());
                    }
                });

    }

    private void init(){
        ButterKnife.bind(this);

        itemLoadListener = this;
        cartLoadListener = this;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recycler_dink.setLayoutManager(gridLayoutManager);
        recycler_dink.addItemDecoration(new SpaceItemDecoration());

        btnCart.setOnClickListener(v -> startActivity(new Intent(this, PayActivity.class)));

    }

    @Override
    public void onItemLoadSuccess(List<ItemsModel> itemsModelList) {
        MyItemAdapter adapter = new MyItemAdapter(this, itemsModelList, cartLoadListener);
        recycler_dink.setAdapter(adapter);

    }

    @Override
    public void onItemLoadFailed(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        int cartSum = 0;
        for (CartModel cartModel: cartModelList){
            cartSum += cartModel.getQuantity();

        }
        badge.setNumber(cartSum);
    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        countCartItem();
    }

    private void countCartItem() {
        List<CartModel> list = new ArrayList<>();
        FirebaseDatabase
                .getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            CartModel cartModel = dataSnapshot.getValue(CartModel.class);
                            cartModel.setKey(dataSnapshot.getKey());
                            list.add(cartModel);
                        }
                        cartLoadListener.onCartLoadSuccess(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }
}