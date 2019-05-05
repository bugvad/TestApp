package dev.bugakov.testapp;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ItemAdapter extends PagedListAdapter<ItemQuestion, ItemAdapter.ItemViewHolder> {

    //адаптер данных для Paging List
    private Context mCtx;

    ItemAdapter(Context mCtx) {
        super(DIFF_CALLBACK);
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemQuestion item = getItem(position);

        if (item != null) {
            holder.textView.setText(String.valueOf(Html.fromHtml(item.title)));
        }
    }

    private static DiffUtil.ItemCallback<ItemQuestion> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ItemQuestion>() {
                @Override
                public boolean areItemsTheSame(ItemQuestion oldItem, ItemQuestion newItem) {
                    return oldItem.question_id == newItem.question_id;
                }

                @Override
                public boolean areContentsTheSame(ItemQuestion oldItem, ItemQuestion newItem) {
                    return oldItem.equals(newItem);
                }
            };

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.name);
        }
    }
}