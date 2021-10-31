
package com.rajendra.onlinedailygroceries.listener;

import com.rajendra.onlinedailygroceries.model.CartModel;
import com.rajendra.onlinedailygroceries.model.ItemsModel;

import java.util.List;

public interface ICartLoadListener {
    void onCartLoadSuccess(List<CartModel> itemModelList);
    void onCartLoadFailed(String message);
}


