package appersiano.subitostargazers.getstargazers;

import java.util.ArrayList;

import appersiano.subitostargazers.data.Stargazer;


public interface GetStargazersContract {

    interface View {

        void showRepoWithNoStargazers();

        void showStargazersList(ArrayList<Stargazer> stargazers);

        void clearUIResults();

        void showProgressBar(boolean value);

        void showOwnerLabelError();

        void showRepoLabelError();

        void showAdditionalInfoText(String message);

        void hideKeyboard();

        void showTostWithMessage(String message);

    }

    interface UserActionsListener {

        void getStargazers(String owner, String repo);

        void loadMoreStargazers();

    }
}
