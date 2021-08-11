package com.example.platform.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.platform.R;
import com.example.platform.activities.CommunityDetailsActivity;
import com.example.platform.models.Community;
import com.parse.ParseFile;

import java.util.List;

public class CommunitiesAdapter extends RecyclerView.Adapter<CommunitiesAdapter.ViewHolder>{

    public static final String TAG = "CommunitiesAdapter";

    Context context;
    List<Community> communities;

    public CommunitiesAdapter(Context context, List<Community> communities) {
        this.context = context;
        this.communities = communities;
    }

    @NonNull
    @Override
    // Inflate the proper xml document
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_community, parent, false);
        return new ViewHolder(view);
    }

    @Override
    // Bind values based on the position of the element in the list
    public void onBindViewHolder(@NonNull CommunitiesAdapter.ViewHolder holder, int position) {
        Community post = communities.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return communities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvDescription;
        ImageView ivCover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName_CommunityItem);
            tvDescription = itemView.findViewById(R.id.tvDescription_CommunityItem);
            ivCover = itemView.findViewById(R.id.ivCover_CommunityItem);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Community community = communities.get(position);
                        Intent intent = new Intent(context, CommunityDetailsActivity.class);
                        intent.putExtra("name", community.getName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        Log.i(TAG, "Opening CommunityDetailsActivity");
                    }
                }
            });
        }

        public void bind(Community community) {
            tvName.setText(community.getName());
            tvDescription.setText(community.getDescription());

            ParseFile image = community.getImage();
            if (image != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .centerInside()
                        .into(ivCover);
            }
        }
    }
}
