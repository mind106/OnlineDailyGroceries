
package com.rajendra.onlinedailygroceries.listener;

import com.rajendra.onlinedailygroceries.model.ItemsModel;

import java.util.List;

public interface IDItemLoadListener {
    void onItemLoadSuccess(List<ItemsModel> itemsModelList);
    void onItemLoadFailed(String message);

}


