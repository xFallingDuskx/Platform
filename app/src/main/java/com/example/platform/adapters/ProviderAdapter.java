package com.example.platform.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.platform.R;
import com.example.platform.activities.CatalogActivity_TitleDisplay;
import com.example.platform.models.Provider;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ViewHolder>{

    public static final String TAG = "ProviderAdapter";

    private Context context;
    private List<Provider> providers;
    FragmentManager fragmentManager;

    public ProviderAdapter(Context context, List<Provider> providers) {
        this.context = context;
        this.providers = providers;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_genre, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Provider provider = providers.get(position);
        holder.bind(provider);
    }

    @Override
    public int getItemCount() {
        return providers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvGenreName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Provider provider = providers.get(position);
                        Intent intent = new Intent(context, CatalogActivity_TitleDisplay.class);
                        intent.putExtra("id", provider.getId());
                        intent.putExtra("type", provider.getType());
                        intent.putExtra("objective", "provider");
                        context.startActivity(intent);
                    }
                }
            });
        }

        public void bind(Provider provider) {
            tvName.setText(provider.getName());
        }
    }
}
