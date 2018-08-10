package ke.co.toshngure.androidbasecode.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import java.util.List;
import java.util.Random;

import ke.co.toshngure.androidbasecode.R;
import ke.co.toshngure.androidbasecode.model.User;
import ke.co.toshngure.basecode.dataloading.DataLoadingConfig;
import ke.co.toshngure.basecode.dataloading.ModelFragment;
import ke.co.toshngure.views.NetworkImage;

public class UserFragment extends ModelFragment<User> {

    public static UserFragment newInstance() {
        
        Bundle args = new Bundle();
        
        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected DataLoadingConfig<User> getDataLoadingConfig() {
        return super.getDataLoadingConfig()
                .withRelativeUrl("/users/"+new Random().nextInt(20), User.class, true)
                .withRefreshEnabled()
                .withDebugEnabled()
                .withTopViewCollapsible();
    }

    @Override
    protected void onDataReady(List<User> data) {
        super.onDataReady(data);
        Snackbar.make(getView(), "Item is ready", Snackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    protected void setUpTopView(FrameLayout topViewContainer) {
        super.setUpTopView(topViewContainer);
        LayoutInflater.from(getActivity()).inflate(R.layout.fragment_users_top_view, topViewContainer);
        NetworkImage topViewNI = topViewContainer.findViewById(R.id.topViewNI);
        topViewNI.loadImageFromNetwork("https://lorempixel.com/400/400/cats/?33483");
    }


    /*@Override
    protected void setUpBottomView(FrameLayout bottomViewContainer) {
        super.setUpBottomView(bottomViewContainer);
        LayoutInflater.from(getActivity()).inflate(R.layout.fragment_users_top_view, bottomViewContainer);
        NetworkImage topViewNI = bottomViewContainer.findViewById(R.id.topViewNI);
        topViewNI.loadImageFromNetwork("https://lorempixel.com/400/400/cats/?33483");
    }*/

    @Override
    protected void onSetUpContentView(FrameLayout contentView) {
        super.onSetUpContentView(contentView);
        LayoutInflater.from(getActivity()).inflate(R.layout.item_user, contentView);
    }
}
