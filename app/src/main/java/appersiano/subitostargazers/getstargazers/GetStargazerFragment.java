package appersiano.subitostargazers.getstargazers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import appersiano.subitostargazers.R;
import appersiano.subitostargazers.data.GithubApiData;
import appersiano.subitostargazers.data.Stargazer;
import appersiano.subitostargazers.util.EndlessRecyclerViewScrollListener;
import appersiano.subitostargazers.util.StargazersAdapter;
import appersiano.subitostargazers.util.Util;

public class GetStargazerFragment extends Fragment implements GetStargazersContract.View {

    private GetStargazersContract.UserActionsListener mActionListener;

    private EditText fOwner, fRepo;
    private ContentLoadingProgressBar progressBar;
    private ImageView additionalInfoImage;
    private TextView additionalInfoText;

    private RecyclerView stargazersRecycler;
    private StargazersAdapter stargazersAdapter;

    private static final String ADDITIONAL_INFO_TEXT = "ADDITIONAL_INFO_TEXT";
    private static final String STARGAZERS_SAVED = "STARGAZERS_SAVED";
    private static final String IMG_ADD_INFO = "IMG_ADD_INFO";

    public GetStargazerFragment() {
        // Required empty public constructor
    }

    public static GetStargazerFragment newInstance() {
        return new GetStargazerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mActionListener = new GetStargazersPresenter(this, new GithubApiData(getContext()), getContext());
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        restoreView(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_getstargazer, container, false);

        Button btnShowStargazers = (Button) root.findViewById(R.id.buttonShowStargazers);

        btnShowStargazers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActionListener.getStargazers(getOwnerString(), getRepoString());
            }

        });

        //Recupero i riferimenti del layout
        fOwner = (EditText) root.findViewById(R.id.fOwner);
        fRepo = (EditText) root.findViewById(R.id.fRepo);
        progressBar = (ContentLoadingProgressBar) root.findViewById(R.id.progressBar);
        stargazersRecycler = (RecyclerView) root.findViewById(R.id.recyclerStargazer);
        additionalInfoImage = (ImageView) root.findViewById(R.id.ivAdditionInfo);
        additionalInfoText = (TextView) root.findViewById(R.id.additionInfoText);

        clearUIResults();
        setupStargazerRecycler();

        return root;
    }

    private void setupStargazerRecycler() {
        stargazersAdapter = new StargazersAdapter(getContext());
        stargazersRecycler.setAdapter(stargazersAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        stargazersRecycler.setLayoutManager(linearLayoutManager);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mActionListener.loadMoreStargazers();
            }
        };

        stargazersRecycler.addOnScrollListener(scrollListener);
    }


    @Override
    public void showRepoWithNoStargazers() {
        additionalInfoImage.setImageDrawable(getContext().getDrawable(R.drawable.nostargazerlogo));
        additionalInfoImage.setTag(R.drawable.nostargazerlogo);

        additionalInfoImage.setVisibility(View.VISIBLE);
        additionalInfoText.setText(R.string.no_one_stargazer);
    }

    @Override
    public void showStargazersList(ArrayList<Stargazer> stargazers) {
        stargazersAdapter.addData(stargazers);
    }

    @Override
    public void clearUIResults() {
        progressBar.hide();

        if (stargazersAdapter != null)
            stargazersAdapter.clearData();

        additionalInfoText.setText("");
        additionalInfoImage.setImageDrawable(null);
        additionalInfoImage.setTag(null);
    }

    @Override
    public void showProgressBar(boolean value) {
        if (value) {
            progressBar.show();
        } else {
            progressBar.hide();
        }
    }

    @Override
    public void showOwnerLabelError() {
        fOwner.setError(getString(R.string.check_the_field));
    }

    @Override
    public void showRepoLabelError() {
        fRepo.setError(getString(R.string.check_the_field));
    }

    @Override
    public void showAdditionalInfoText(String message) {
        additionalInfoText.setText(message);
    }

    @Override
    public void hideKeyboard() {
        Util.hideKeyboard(getActivity(), getContext());
    }

    @Override
    public void showTostWithMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    private String getOwnerString() {
        return fOwner.getText().toString().trim();
    }

    @NonNull
    private String getRepoString() {
        return fRepo.getText().toString().trim();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        state.putString(ADDITIONAL_INFO_TEXT, additionalInfoText.getText().toString());

        if (stargazersAdapter.getmStargazers() != null && stargazersAdapter.getmStargazers().size() > 0) {
            ArrayList<Stargazer> myList = new ArrayList<>(stargazersAdapter.getmStargazers());
            state.putSerializable(STARGAZERS_SAVED, myList);
        }

        if (additionalInfoImage != null && additionalInfoImage.getTag() != null) {
            int drawableId = Integer.parseInt(additionalInfoImage.getTag().toString());
            state.putInt(IMG_ADD_INFO, drawableId);
        }

    }


    private void restoreView(@Nullable Bundle state) {

        if (state != null) {
            additionalInfoText.setText(state.getString(ADDITIONAL_INFO_TEXT));

            ArrayList<Stargazer> stargazersArrayList = (ArrayList<Stargazer>) state.getSerializable(STARGAZERS_SAVED);
            if (stargazersArrayList != null)
                stargazersAdapter.addData(stargazersArrayList);

            int drawableIdAdditionalInfo = state.getInt(IMG_ADD_INFO);
            if (drawableIdAdditionalInfo != 0) {
                additionalInfoImage.setImageDrawable(getContext().getDrawable(drawableIdAdditionalInfo));
                additionalInfoImage.setTag(drawableIdAdditionalInfo);
            }
        }

    }
}

