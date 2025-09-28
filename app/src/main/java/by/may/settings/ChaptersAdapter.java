package by.may.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import by.may.R;

public class ChaptersAdapter extends RecyclerView.Adapter<ChaptersAdapter.Holder> {

    private ArrayList<String> list;

    public ChaptersAdapter(ArrayList<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        holder.image.setImageResource(R.drawable.lock);
        holder.textView.setText(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView image;

        public Holder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.itemTitle);
            image = itemView.findViewById(R.id.lock);

        }
    }

}
