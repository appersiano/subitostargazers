package appersiano.subitostargazers.getstargazers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

import appersiano.subitostargazers.R;
import appersiano.subitostargazers.data.GithubApiData;
import appersiano.subitostargazers.data.Stargazer;
import appersiano.subitostargazers.util.EspressoIdlingResource;
import appersiano.subitostargazers.util.Util;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetStargazersPresenter implements GetStargazersContract.UserActionsListener {

    public static final String NEXT = "next";

    @NonNull
    private final GetStargazersContract.View mGetStargazersView;

    private Context mContext;
    private GithubApiData mGithubApiData;
    private HashMap<String, String> linkHash;
    private String owner;
    private String repo;


    private Callback<ArrayList<Stargazer>> callbackGithub = new Callback<ArrayList<Stargazer>>() {

        @Override
        public void onResponse(Call<ArrayList<Stargazer>> call, Response<ArrayList<Stargazer>> response) {
            ArrayList<Stargazer> stargazersArrayList;

            mGetStargazersView.showProgressBar(false);

            if (response.isSuccessful() && response.body() != null) {

                linkHash = getLinksHeader(response.headers());
                stargazersArrayList = response.body();

                if (stargazersArrayList.size() > 0) {
                    mGetStargazersView.showAdditionalInfoText(String.format(mContext.getString(R.string.starg_of_repo_by_owner), repo, owner));
                    mGetStargazersView.showStargazersList(stargazersArrayList);
                } else {
                    mGetStargazersView.showRepoWithNoStargazers();
                }
            } else {
                switch (response.code()) {
                    case 404:
                        mGetStargazersView.showAdditionalInfoText(mContext.getString(R.string.repo_not_found_404));
                        break;
                    default:
                        mGetStargazersView.showAdditionalInfoText(mContext.getString(R.string.general_error_message));
                }
            }

            EspressoIdlingResource.decrement();
        }

        @Override
        public void onFailure(Call<ArrayList<Stargazer>> call, Throwable t) {
            mGetStargazersView.showProgressBar(false);
            mGetStargazersView.showAdditionalInfoText(mContext.getString(R.string.general_error_message));

            EspressoIdlingResource.decrement();
        }
    };

    public GetStargazersPresenter(@NonNull GetStargazersContract.View getStargazersView, GithubApiData githubApiData, Context context) {
        mGetStargazersView = getStargazersView;
        mGithubApiData = githubApiData;
        mContext = context;
    }

    //Ho sdoppiato volutamente la logica nel caso vadano inserite
    //regole di validazione personalizzate per l'owner o il repo
    public static boolean ownerIsValid(String owner) {
        return !owner.isEmpty() && !owner.contains(" ");
    }

    private static boolean repoIsValid(String repo) {
        return !repo.isEmpty() && !repo.contains(" ");
    }

    /**
     * Restituisce una coppia contenente il prossimo path e numero di pagine
     * Es: path -> "repositories/61999847/stargazers"
     * pageNumber -> "2"
     *
     * @return Pair composta da path e numero pagina
     */
    public static Pair<String, String> getNextPathAndPagePair(HashMap<String, String> linkHash) {
        if (linkHash != null && linkHash.containsKey(NEXT)) {
            String[] splitNextString = linkHash.get(NEXT).split("[?]");

            String path = splitNextString[0];
            String pageNumber = splitNextString[1].replaceAll("[^0-9]", "");

            return Pair.create(path, pageNumber);
        } else {
            return null;
        }
    }

    @Override
    public void getStargazers(String owner, String repo) {
        mGetStargazersView.hideKeyboard();
        mGetStargazersView.clearUIResults();

        if (!Util.isConnected(mContext)) {
            mGetStargazersView.showAdditionalInfoText(mContext.getString(R.string.offline_message));
            return;
        }

        if (areOwnerAndRepoValid(owner, repo)) {
            // The IdlingResource is null in production.
            EspressoIdlingResource.increment();

            mGetStargazersView.showProgressBar(true);
            this.owner = owner;
            this.repo = repo;
            mGithubApiData.getStargazerOfRepo(this.owner, this.repo, callbackGithub);

        } else {
            //La validazione non è andato a buon fine, mostra gli errori
            if (!ownerIsValid(owner))
                mGetStargazersView.showOwnerLabelError();

            if (!repoIsValid(repo))
                mGetStargazersView.showRepoLabelError();
        }
    }

    @Override
    public void loadMoreStargazers() {
        Pair<String, String> nextPathAndPagePair = getNextPathAndPagePair(linkHash);

        if (nextPathAndPagePair != null) {
            EspressoIdlingResource.increment();
            mGithubApiData.getStargazerOfRepByPage(nextPathAndPagePair.first, nextPathAndPagePair.second, callbackGithub);
            mGetStargazersView.showTostWithMessage(String.format(mContext.getString(R.string.loading_page), nextPathAndPagePair.second));
        }
    }

    /**
     * Indica sei i dati di input owner e repository hanno un formato valido
     *
     * @return true -> formato valido false -> formato NON valido
     */
    private boolean areOwnerAndRepoValid(String owner, String repo) {
        return (ownerIsValid(owner) && repoIsValid(repo));
    }

    /**
     * Recupera l'header "Link" della response http e la elabora, essendo il formato del tipo
     * <https://api.github.com/resource?page=2>; rel="next", <https://api.github.com/resource?page=5>; rel="last"
     * viene tutto trasformato in un hashmap con key il valore dell'attributo rel e value l'endpoint pulito
     * nel nostro esempio:
     * "next" -> "resource?page=2"
     * "last" -> "resource?page=5
     *
     * @param headers
     */
    private HashMap<String, String> getLinksHeader(Headers headers) {
        String link = headers.get("Link");

        if (link != null && !link.isEmpty()) {
            String[] linkSplitted = link.split(",");

            HashMap<String, String> linkHash = new HashMap<>();
            for (String string : linkSplitted) {
                String[] singleLink = string.split(";");
                linkHash.put(
                        singleLink[1].trim().replaceAll("rel=\"", "").replaceAll("\"", ""),
                        singleLink[0].trim().replaceAll("[<|>]", "").replace(mContext.getString(R.string.endpoint_api_github), ""));
            }

            return linkHash;
        } else {
            return null;
        }
    }
}
