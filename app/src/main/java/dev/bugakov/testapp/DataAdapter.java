package dev.bugakov.testapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    //адаптер данных для получение 100 заголовков (без загрузки постранично как в Page Library)
    //API позволяет все загрузить 100 заголовков за 1 запрос и приложение не простаивает,
    //поэтому не Page Library

    private LayoutInflater inflater;
    private List<Item> itemsList;

    DataAdapter(Context context, List<Item> itemsList) {
        this.itemsList = itemsList;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder holder, int position) {
        Item item = itemsList.get(position);
        holder.nameView.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        ViewHolder(View view){
            super(view);
            nameView = (TextView) view.findViewById(R.id.name);
        }
    }
}