package appersiano.subitostargazers.data;

import android.content.Context;

import java.util.ArrayList;

import appersiano.subitostargazers.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by alessandro.persiano on 30/12/2016.
 */

public class GithubApiData {


    private GitHubService service = null;

    public GithubApiData(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.endpoint_api_github))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(GitHubService.class);
    }

    public interface GitHubService {
        @GET("repos/{owner}/{repo}/stargazers")
        Call<ArrayList<Stargazer>> listStargazerOfRepo(@Path("owner") String owner, @Path("repo") String repo);

        @GET("/{nextpage}")
        Call<ArrayList<Stargazer>> listStargazerOfRepoByPage(@Path(value = "nextpage", encoded = true) String nextPage, @Query("page") String page);
    }

    public void getStargazerOfRepo(String owner, String repo, Callback<ArrayList<Stargazer>> callback){
        Call<ArrayList<Stargazer>> listCall = service.listStargazerOfRepo(owner, repo);
        listCall.enqueue(callback);
    }

    public void getStargazerOfRepByPage(String nextPagePath, String pageNumber, Callback<ArrayList<Stargazer>> callback){
        Call<ArrayList<Stargazer>> listCall = service.listStargazerOfRepoByPage(nextPagePath, pageNumber);
        listCall.enqueue(callback);
    }

}
