package com.ninos.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ninos.R;
import com.ninos.listeners.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smeesala on 6/30/2017.
 */

public abstract class CommonRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = CommonRecyclerAdapter.class.getSimpleName();

    private final int VISIBLE_THRESHOLD = 1;
    private final int ITEM_VIEW_TYPE_BASIC = 0;
    private final int ITEM_VIEW_TYPE_FOOTER = 1;
    private List<T> dataSet;
    private int lastVisibleItem, totalItemCount, previousTotal = 0;
    private boolean loading = true;

    public CommonRecyclerAdapter(RecyclerView recyclerView, final OnLoadMoreListener onLoadMoreListener) {
        try {
            dataSet = new ArrayList<>();
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                        if (loading && (totalItemCount > previousTotal)) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }

                        if (!loading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    }

                });
            }
        } catch (Exception e) {
            Log.e(TAG, "CommonRecyclerAdapter() - " + e.toString(), e);
        }
    }

    public CommonRecyclerAdapter() {
        dataSet = new ArrayList<>();
    }

    /**
     * reset the dataset and add new dataset to the list
     *
     * @param newDataSet list of new data set
     */
    public void resetItems(@NonNull List<T> newDataSet) {
        loading = false;
        lastVisibleItem = 0;
        totalItemCount = 0;
        previousTotal = 0;
        dataSet.clear();
        addItems(newDataSet);
        notifyDataSetChanged();
    }

    public void resetItems() {
        loading = false;
        lastVisibleItem = 0;
        totalItemCount = 0;
        previousTotal = 0;
        dataSet.clear();
        notifyDataSetChanged();
    }

    /**
     * clears the items in the data set
     */
    public void clearItemsforSearch() {
        dataSet.clear();
        notifyDataSetChanged();
    }

    /**
     * adds list of items in the data set
     *
     * @param newDataSetItems list of items
     */
    public void addItems(@NonNull List<T> newDataSetItems) {
        dataSet.addAll(newDataSetItems);
        notifyDataSetChanged();
    }

    /**
     * adds single item in the data set
     *
     * @param item to be added
     */
    public void addItem(T item) {
        if (!dataSet.contains(item)) {
            dataSet.add(item);
            notifyItemInserted(dataSet.size() - 1);
        }
    }

    /**
     * adds single item in the data set at position
     *
     * @param item     to be added
     * @param position item to be added at position
     */
    public void addItem(T item, int position) {
        if (!dataSet.contains(item)) {
            dataSet.add(position, item);
            notifyItemInserted(position);
        }
    }

    /**
     * remove an item from data set
     *
     * @param item item to be removed
     */
    public void removeItem(T item) {
        int indexOfItem = dataSet.indexOf(item);
        if (indexOfItem != -1) {
            this.dataSet.remove(indexOfItem);
            notifyItemRemoved(indexOfItem);
        }
    }

    public void removeItem(int indexOfItem) {
        if (indexOfItem != -1) {
            this.dataSet.remove(indexOfItem);
            notifyItemRemoved(indexOfItem);
        }
    }

    public void hideLoading() {
        loading = false;
    }

    /**
     * get item by its position
     *
     * @param index
     * @return
     */
    public T getItem(int index) {
        if (dataSet != null && dataSet.size() >= index && dataSet.get(index) != null) {
            return dataSet.get(index);
        } else {
            return null;
        }
    }

    /**
     * updates item at given position
     *
     * @param position of item to be updated
     */
    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    public void updateItem(int position, T item) {
        dataSet.set(position, item);
        notifyItemChanged(position);
    }

    /**
     * get type of item at given position (used to show/remove progressbar at bottom )
     *
     * @param position position of item
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position) != null ? ITEM_VIEW_TYPE_BASIC : ITEM_VIEW_TYPE_FOOTER;
    }

    /**
     * gets the count of item
     *
     * @return list count
     */
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    /**
     * Sets the custom layout for list item
     *
     * @param parent   contains views of custom layout
     * @param viewType progress bar (or) custom view
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_BASIC) {
            return onCreateBasicItemViewHolder(parent, viewType);
        } else if (viewType == ITEM_VIEW_TYPE_FOOTER) {
            return onCreateFooterViewHolder(parent);
        } else {
            Log.w(TAG, "Invalid type, this type of items " + viewType + " can't be handled");
            throw new IllegalStateException("Invalid type, this type of items " + viewType + " can't be handled");
        }
    }

    /**
     * binding the view to the viewholder
     *
     * @param genericHolder describes an item view
     * @param position      type of item(progress bar (or) list item)
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder genericHolder, int position) {
        if (getItemViewType(position) == ITEM_VIEW_TYPE_BASIC) {
            onBindBasicItemView(genericHolder, position);
        } else {
            onBindFooterView(genericHolder);
        }
    }

    /**
     * called when onCreateViewHolder is called
     *
     * @param parent
     * @param viewType
     * @return
     */
    public abstract RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType);

    /**
     * called when onBindViewHolder is called
     *
     * @param genericHolder
     * @param position
     */
    public abstract void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position);

    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress_bar, parent, false);
        return new ProgressViewHolder(view);
    }

    public void onBindFooterView(RecyclerView.ViewHolder genericHolder) {
        ((ProgressViewHolder) genericHolder).progressBar.setIndeterminate(true);
    }

    /**
     * view holder for progressbar
     */
    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progress_bar);
        }
    }

}