package com.mauriciogiordano.travell;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mauriciogiordano.travell.adapter.ItineraryAdapter;
import com.mauriciogiordano.travell.model.City;

import java.util.ArrayList;
import java.util.List;


public class ItineraryActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar_background));
        }

        ListView listView = (ListView) findViewById(R.id.listView);
        final ItineraryAdapter adapter = new ItineraryAdapter();

        listView.setAdapter(adapter);

        List<City> dataList = new ArrayList<>();
        dataList.add(new City("São Paulo", "http://claritur.com.br/site/wp-content/uploads/2013/09/sp_groupon.jpg", this));
        dataList.add(new City("Rio de Janeiro", "http://blog.encontresuaviagem.com.br/wp-content/uploads/2015/05/Rio-de-Janeiro.jpg", this));

        adapter.setDataList(dataList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                City city = (City) adapter.getItem(i);
                city.save();

                Intent intent = new Intent(ItineraryActivity.this, SwipeActivity.class);
                intent.putExtra("cityId", city.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_itinerary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
