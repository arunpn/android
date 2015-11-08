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
import android.widget.Toast;

import com.mauriciogiordano.travell.adapter.DestinationAdapter;
import com.mauriciogiordano.travell.api.Delegate;
import com.mauriciogiordano.travell.model.Destination;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DestinationActivity extends ActionBarActivity {

    private DestinationAdapter adapter;
    private View progressBar;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar_background));
        }

        listView = (ListView) findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        adapter = new DestinationAdapter();

        listView.setAdapter(adapter);

        loadDestinations();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Destination destination = (Destination) adapter.getItem(i);
                destination.save();

                Intent intent = new Intent(DestinationActivity.this, SwipeActivity.class);
                intent.putExtra("destinationId", destination.getId());
                startActivity(intent);
            }
        });
    }

    private void loadDestinations() {
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        Destination.getTopDestinations(new Delegate() {
            @Override
            public void requestResults(boolean hasInternet, HttpResponse response, JSONObject result) {
                progressBar.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

                if (hasInternet) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        try {
                            JSONArray jsonArray = result.getJSONArray("data");

                            List<Destination> dataList = new ArrayList<>();

                            for (int i=0; i<jsonArray.length(); i++) {
                                Destination d = new Destination(jsonArray.getJSONObject(i), DestinationActivity.this);

                                dataList.add(d);
                            }

                            adapter.setDataList(dataList);
                        } catch (JSONException e) {

                        }
                    } else {
                        Toast.makeText(DestinationActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DestinationActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
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
