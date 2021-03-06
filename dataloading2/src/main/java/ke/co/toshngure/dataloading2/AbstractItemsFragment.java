package ke.co.toshngure.dataloading2;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.core.content.ContextCompat;
import androidx.loader.content.Loader;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

abstract class AbstractItemsFragment<M> extends Fragment
        implements LoaderManager.LoaderCallbacks<List<M>>,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ModelFragment";

    private DataLoadingConfig<M> mDataLoadingConfig;

    /**
     * Loading View
     */
    private LinearLayout loadingLL;
    private TextView loadingTV;

    /**
     * Error View
     */
    private LinearLayout errorLL;
    private TextView errorTV;
    private FrameLayout freshLoadContainer;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isLoading = false;
    protected FrameLayout mBottomViewContainer;
    protected FrameLayout mTopViewContainer;
    private M mData;
    private List<M> mDataList = null;
    private View errorIV;


    protected DataLoadingConfig<M> getDataLoadingConfig() {
        return new DataLoadingConfig<>();
    }

    protected RequestParams getRequestParams() {
        return new RequestParams();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataLoadingConfig = getDataLoadingConfig();
    }

    protected List<M> onLoadCache() {
        return null;
    }

    protected void onDataReady(M data) {
        this.mData = data;
        mSwipeRefreshLayout.setRefreshing(false);
        onDataReady(data != null);
    }

    protected void onDataReady(List<M> data) {
        this.mDataList = data;
        mSwipeRefreshLayout.setRefreshing(false);
        onDataReady(data.size() > 0);
    }

    private void onDataReady(boolean dataIsAvailable){
        if (!dataIsAvailable){
            mSwipeRefreshLayout.setVisibility(View.GONE);
            freshLoadContainer.setVisibility(View.VISIBLE);
            errorLL.setVisibility(View.VISIBLE);
            errorLL.setOnClickListener(view1 -> connect());
            loadingLL.setVisibility(View.GONE);
            errorIV.setVisibility(mDataLoadingConfig.getMessageIconVisibility());
            errorTV.setText(mDataLoadingConfig.getEmptyDataMessage());
            errorTV.setTextColor(ContextCompat.getColor(errorTV.getContext(),
                    mDataLoadingConfig.getEmptyDataMessageColor()));
        } else {
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            freshLoadContainer.setVisibility(View.GONE);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        //Load cache data
        if (mDataLoadingConfig.isCacheEnabled()) {
            getActivity().getSupportLoaderManager().initLoader(mDataLoadingConfig.getLoaderId(),
                    null, this);
        } else if (mDataLoadingConfig.isAutoRefreshEnabled()) {
            connect();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if (mDataLoadingConfig.isTopViewCollapsible()) {
            view = inflater.inflate(R.layout.fragment_item_collapsible, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_item_not_collapsible, container, false);
        }

        //Configure Swipe Refresh Layout
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        mSwipeRefreshLayout.setEnabled(mDataLoadingConfig.isRefreshEnabled());
        mSwipeRefreshLayout.setOnRefreshListener(this);
        onSetUpSwipeRefreshLayout(mSwipeRefreshLayout);


        //Configure mContentView
        FrameLayout contentView = view.findViewById(R.id.contentView);
        onSetUpContentView(contentView);

        setUpTopView(view.findViewById(R.id.topViewContainer));
        setUpBottomView(view.findViewById(R.id.bottomViewContainer));
        setUpBackground(view.findViewById(R.id.backgroundIV));


        this.freshLoadContainer = view.findViewById(R.id.freshLoadContainer);
        this.freshLoadContainer.setVisibility(View.GONE);

        this.loadingLL = view.findViewById(R.id.loadingLL);
        this.loadingTV = view.findViewById(R.id.loadingTV);
        this.loadingTV.setText(mDataLoadingConfig.getLoadingMessage());
        this.loadingTV.setTextColor(ContextCompat.getColor(loadingTV.getContext(),
                mDataLoadingConfig.getLoadingMessageColor()));

        this.errorLL = view.findViewById(R.id.errorLL);
        this.errorTV = view.findViewById(R.id.errorTV);
        this.errorIV = view.findViewById(R.id.errorIV);

        return view;
    }

    protected void setUpBackground(ImageView backgroundIV) {

    }

    protected void setUpBottomView(FrameLayout bottomViewContainer) {
        this.mBottomViewContainer = bottomViewContainer;
    }

    protected void setUpTopView(FrameLayout topViewContainer) {
        this.mTopViewContainer = topViewContainer;

    }

    protected void onSetUpContentView(FrameLayout contentView) {

    }

    protected void onSetUpSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
    }

    private void connect() {
        log("connect");
        RequestParams requestParams = getRequestParams();
        log("Params : " + requestParams.toString());
        log("Url : " + mDataLoadingConfig.getUrl());
        mDataLoadingConfig.getAsyncHttpClient().get(getActivity(), mDataLoadingConfig.getUrl(),
                requestParams, new ResponseHandler());
    }

    private void log(Object msg) {
        if (mDataLoadingConfig != null && mDataLoadingConfig.isDebugEnabled()) {
            Log.d(TAG, String.valueOf(msg));
        }
    }

    private static class CacheLoader<M> extends DataLoader<List<M>> {

        private final AbstractItemsFragment<M> modelFragment;

        CacheLoader(Context context, AbstractItemsFragment<M> modelFragment) {
            super(context);
            this.modelFragment = modelFragment;
        }

        @Override
        public List<M> onLoad() {
            return modelFragment.onLoadCache();
        }
    }

    @NonNull
    @Override
    public Loader<List<M>> onCreateLoader(int id, @Nullable Bundle args) {
        return new CacheLoader<>(getActivity(), this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<M>> loader, List<M> data) {
        if (loadsSingleItem() && data.size() > 0){
            onDataReady(data.get(0));
        } else {
            onDataReady(data);
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<List<M>> loader) {

    }

    @Override
    public void onRefresh() {
        if (mDataLoadingConfig.isRefreshEnabled()) {
            mSwipeRefreshLayout.setRefreshing(true);
            connect();
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private class ResponseHandler extends JsonHttpResponseHandler {

        @Override
        public void onStart() {
            super.onStart();
            if (mData == null && mDataList == null) {
                mSwipeRefreshLayout.setVisibility(View.GONE);
                freshLoadContainer.setVisibility(View.VISIBLE);
                errorLL.setVisibility(View.GONE);
                loadingLL.setVisibility(View.VISIBLE);
            } else {
                //This is when AutoRefresh is enabled and there is cache
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            super.onProgress(bytesWritten, totalSize);
            double progress = (totalSize > 0) ? (bytesWritten * 1.0 / totalSize) * 100 : -1;
            Log.d(TAG, "Progress = " + progress);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            try {
                if (loadsSingleItem() && mDataLoadingConfig.getDataKey() != null) {
                    //Loads single item with a data key
                    JSONObject itemObject = response.getJSONObject(mDataLoadingConfig.getDataKey());
                    M item = Utils.getSafeGson().fromJson(itemObject.toString(), mDataLoadingConfig.getModelClass());
                    onDataReady(item);
                } else if (loadsSingleItem() && mDataLoadingConfig.getDataKey() == null) {
                    //Loads single item without a data key
                    M item = Utils.getSafeGson().fromJson(response.toString(), mDataLoadingConfig.getModelClass());
                    onDataReady(item);
                } else if (mDataLoadingConfig.getDataKey() != null) {
                    //Loads multiple item with a data key
                    JSONArray jsonArray = response.getJSONArray(mDataLoadingConfig.getDataKey());
                    List<M> items = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        M item = Utils.getSafeGson().fromJson(jsonArray.getJSONObject(i).toString(), mDataLoadingConfig.getModelClass());
                        items.add(item);
                    }
                    onDataReady(items);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            super.onSuccess(statusCode, headers, response);
            try {
                List<M> items = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    M item = Utils.getSafeGson().fromJson(response.getJSONObject(i).toString(), mDataLoadingConfig.getModelClass());
                    items.add(item);
                }
                onDataReady(items);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRetry(int retryNo) {
            super.onRetry(retryNo);
            Log.i(TAG, "onRetry = " + retryNo);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            onError(statusCode, String.valueOf(responseString));
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            String message = loadingTV.getContext().getString(R.string.message_connection_error);
            if (errorResponse != null) {
                try {
                    if (statusCode == 422 && errorResponse.get(DataParserTask.DATA) instanceof JSONObject) {
                        JSONObject data = errorResponse.getJSONObject(DataParserTask.DATA);
                        StringBuilder sb = new StringBuilder();
                        Iterator<String> iterator = data.keys();
                        while (iterator != null && iterator.hasNext()) {
                            String name = iterator.next();
                            try {
                                JSONArray valueErrors = data.getJSONArray(name);
                                sb.append(name.toUpperCase()).append("\n");
                                for (int i = 0; i < valueErrors.length(); i++) {
                                    sb.append(valueErrors.get(i)).append("\n");
                                }
                            } catch (JSONException e) {
                                message = String.valueOf(errorResponse);
                            }
                        }

                        message = sb.toString();

                    } else {
                        JSONObject meta = errorResponse.getJSONObject(DataParserTask.META);
                        message = meta.getString(DataParserTask.MESSAGE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            onError(statusCode, String.valueOf(message));
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            onError(statusCode, String.valueOf(errorResponse));
        }

        private void onError(int statusCode, String error) {
            if (mData == null && mDataList == null) {
                mSwipeRefreshLayout.setVisibility(View.GONE);
                freshLoadContainer.setVisibility(View.VISIBLE);
                errorLL.setVisibility(View.VISIBLE);
                errorLL.setOnClickListener(view1 -> connect());
                loadingLL.setVisibility(View.GONE);
                errorTV.setText(error);
                errorTV.setTextColor(ContextCompat.getColor(errorTV.getContext(),
                        mDataLoadingConfig.getErrorMessageColor()));
                Log.e(TAG, String.valueOf("StatusCode = " + statusCode + ", ERROR: " + error));
            } else if (mSwipeRefreshLayout.isRefreshing()) {
                Snackbar.make(mSwipeRefreshLayout, R.string.unable_to_refresh, Snackbar.LENGTH_LONG);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    abstract boolean loadsSingleItem();
}
