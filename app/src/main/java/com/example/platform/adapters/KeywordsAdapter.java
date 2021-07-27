package com.example.platform.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.platform.R;
import com.example.platform.models.Keyword;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KeywordsAdapter extends RecyclerView.Adapter<KeywordsAdapter.ViewHolder> {

    public static final String TAG = "KeywordsAdapter";

    private Context context;
    private List<Keyword> keywords;

    public KeywordsAdapter(Context context, List<Keyword> keywords) {
        this.context = context;
        this.keywords = keywords;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_keyword_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Keyword keyword = keywords.get(position);
        holder.bind(keyword);
    }

    @Override
    public int getItemCount() {
        return keywords.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvText;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvKeywordText);
        }

        public void bind(Keyword keyword) {
            Log.i(TAG, "Binding for the keyword: " + keyword.getText());
            tvText.setText(keyword.getText());
        }
    }
}
