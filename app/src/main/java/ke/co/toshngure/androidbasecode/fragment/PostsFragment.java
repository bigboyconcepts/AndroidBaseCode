package ke.co.toshngure.androidbasecode.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ke.co.toshngure.androidbasecode.R;
import ke.co.toshngure.androidbasecode.model.Post;
import ke.co.toshngure.basecode.app.BaseAppActivity;
import ke.co.toshngure.basecode.networking.RESTClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostsFragment extends Fragment {


    @BindView(R.id.connectBtn)
    Button connectBtn;
    @BindView(R.id.contentTV)
    TextView contentTV;
    Unbinder unbinder;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment newInstance() {

        Bundle args = new Bundle();

        PostsFragment fragment = new PostsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick(R.id.connectBtn)
    public void onConnectBtnClicked() {
        RESTClient.getInstance().index(Post.class, new RESTClient.Callback<Post>((BaseAppActivity) getActivity(), Post.class) {

            @Override
            protected void onResponse(List<Post> items, @Nullable JSONObject meta) {
                super.onResponse(items, meta);
            }
        });
    }

}