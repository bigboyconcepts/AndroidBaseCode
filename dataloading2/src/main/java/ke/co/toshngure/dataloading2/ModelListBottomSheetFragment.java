/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.dataloading2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.loopj.android.http.RequestParams;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anthony Ngure on 04/06/2017.
 * Email : anthonyngure25@gmail.com.
 */

/**
 * If the response is a {@link org.json.JSONObject} then the list of items should be in a key named data
 * If the response is a {@link org.json.JSONObject} then the list of items should be in a key named cursors
 *
 * @param <M>>
 */


public class ModelListBottomSheetFragment<M extends AbstractItem<M, ?>> extends BottomSheetDialogFragment
        implements DataLoadingFragmentImpl.Listener<M> {


    protected FastItemAdapter<M> mFastItemAdapter;
    protected RecyclerView mRecyclerView;
    protected FrameLayout mBottomViewContainer;
    protected FrameLayout mTopViewContainer;
    protected ImageView mBackgroundIV;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    private DataLoadingFragmentImpl<M> mDataLoadingFragmentImpl;

    public ModelListBottomSheetFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataLoadingFragmentImpl = new DataLoadingFragmentImpl<>(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mDataLoadingFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mDataLoadingFragmentImpl.onStart();
    }

    @Override
    public DataLoadingConfig<M> getDataLoadingConfig() {
        return new DataLoadingConfig<>();
    }


    @Override
    public void setUpTopView(FrameLayout topViewContainer) {
        this.mTopViewContainer = topViewContainer;
    }

    @Override
    public void setUpBackground(ImageView backgroundIV) {
        this.mBackgroundIV = backgroundIV;
    }

    @Override
    public void setUpBottomView(FrameLayout bottomViewContainer) {
        this.mBottomViewContainer = bottomViewContainer;
    }

    @Override
    public List<M> onLoadCaches() {

        return new ArrayList<>();
    }

    @Override
    public RequestParams getRequestParams() {
        return new RequestParams();
    }

    @Override
    public void onSaveItem(M item) {

    }

    @Override
    public int getFreshLoadGravity() {
        return Gravity.TOP | Gravity.CENTER;
    }

    public int addUniqueCacheKey() {
        return 0;
    }

    @Override
    public void onSetUpSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        this.mSwipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    public void onSetUpRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    @Override
    public void onSetUpAdapter(FastItemAdapter<M> fastItemAdapter) {
        this.mFastItemAdapter = fastItemAdapter;
    }


}