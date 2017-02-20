package appersiano.subitostargazers.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import appersiano.subitostargazers.R;
import appersiano.subitostargazers.data.Stargazer;

public class StargazersAdapter extends RecyclerView.Adapter<StargazersAdapter.ViewHolder> {

    private List<Stargazer> mStargazers;
    private Context mContext;

    public List<Stargazer> getmStargazers() {
        return mStargazers;
    }

    public StargazersAdapter(Context context) {
        mStargazers = null;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View stargazerView = inflater.inflate(R.layout.item_stargazer, parent, false);

        return new ViewHolder(stargazerView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Stargazer stargazer = mStargazers.get(position);

        Picasso.with(mContext)
                .load(stargazer.getAvatarUrl())
                .into(holder.avatarImage);

        holder.stargazerUsername.setText(stargazer.getLogin());
    }

    @Override
    public int getItemCount() {
        if (mStargazers != null)
            return mStargazers.size();
        else
            return 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImage;
        TextView stargazerUsername;

        public ViewHolder(View itemView) {
            super(itemView);

            avatarImage = (ImageView) itemView.findViewById(R.id.avatarImage);
            stargazerUsername = (TextView) itemView.findViewById(R.id.stargazerName);
        }
    }

    public void clearData() {
        if (mStargazers != null) {
            mStargazers.clear();
            notifyDataSetChanged();
        }
    }

    public void addData(ArrayList<Stargazer> stargazers) {
        if (mStargazers == null) {
            mStargazers = new ArrayList<>();
        }
            mStargazers.addAll(stargazers);
            notifyDataSetChanged();
    }
}
