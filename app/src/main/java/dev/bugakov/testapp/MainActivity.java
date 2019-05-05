package dev.bugakov.testapp;

import android.app.FragmentManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static boolean isNetworkAvailable(final Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper dbHelper = new DBHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        RecyclerView recyclerView = findViewById(R.id.list);

        //разделитель
        DividerItemDecoration itemDecorator = new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.divider));
        recyclerView.addItemDecoration(itemDecorator);

        getList(recyclerView, db);

        //swipe-to-refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getList(recyclerView, db);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //получить заголовки за последнюю неделю
    public void getWeekList (RecyclerView recyclerView)
    {
        DBHelper dbHelper = new DBHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("myTable", null, null);

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setHasFixedSize(true);

        ItemViewModel itemViewModel = ViewModelProviders.of(MainActivity.this).get(ItemViewModel.class);
        final ItemAdapter adapter = new ItemAdapter(MainActivity.this);

        itemViewModel.itemPagedList.observe(MainActivity.this, new Observer<PagedList<ItemQuestion>>() {
            @Override
            public void onChanged(@Nullable PagedList<ItemQuestion> items) {

                //in case of any changes
                //submitting the items to adapter
                adapter.submitList(items);

            }
        });

        recyclerView.setAdapter(adapter);
    }

    //проверки и получение
    public void getList(RecyclerView recyclerView, SQLiteDatabase db)
    {

        //проверка на наличие сети
        if (isNetworkAvailable(getApplicationContext())) {

            ConnectivityManager manager = (ConnectivityManager)
                    getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo info = manager.getActiveNetworkInfo();

            //проверка на скорость соединения
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE &&
                    info.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS) {

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Слабое подключение", Toast.LENGTH_SHORT);
                toast.show();

            }

            //запрос первых 100 заголовков
            RetrofitClient.getInstance()
                    .getApi().getAnswersMain(1, 100, "desc", "activity", "android", "stackoverflow")
                    .enqueue(new Callback<StackApiResponse>() {



                        @Override
                        public void onResponse(Call<StackApiResponse> call, Response<StackApiResponse> response) {
                            List<ItemQuestion> list = null;
                            List<Item> item = new ArrayList<>();

                            if (response.body() != null) {
                                list = response.body().items;

                                if (list.size() != 100)
                                {
                                    //получение заголовков за последнюю неделю, если меньше 100 заголовков
                                    getWeekList(recyclerView);
                                }
                                else
                                {

                                    db.delete("myTable", null, null);

                                    DataAdapter adapterMain = new DataAdapter(MainActivity.this, item);

                                    //кеширование
                                    for (ItemQuestion list_item: list) {
                                        ContentValues cv = new ContentValues();
                                        String mBuf = String.valueOf(Html.fromHtml(list_item.title));
                                        item.add(new Item(mBuf));
                                        cv.put("title", mBuf);
                                        db.insert("myTable", null, cv);
                                    }

                                    adapterMain.notifyDataSetChanged();
                                    recyclerView.setAdapter(adapterMain);
                                }
                            }
                            else if (response.body() == null)
                            {
                                //получение заголовков за последнюю неделю, если ответ на запрос пуст
                                getWeekList(recyclerView);
                            }
                        }

                        @Override
                        public void onFailure(Call<StackApiResponse> call, Throwable t) {

                        }
                    });
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Нет сети, отображены сохраненные данные", Toast.LENGTH_SHORT);
            toast.show();
            List<Item> item = new ArrayList<>();

            //чтение кешированных данных
            Cursor c = db.query("myTable", null, null, null, null, null, null);

            if (c.moveToFirst()) {

                int titleColIndex = c.getColumnIndex("title");

                do {
                    item.add(new Item(c.getString(titleColIndex)));
                } while (c.moveToNext());
            }
            DataAdapter adapterMain = new DataAdapter(MainActivity.this, item);
            adapterMain.notifyDataSetChanged();
            recyclerView.setAdapter(adapterMain);

        }
    }
}