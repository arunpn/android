package com.mauriciogiordano.travell;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mauriciogiordano.travell.adapter.DestinationAdapter;
import com.mauriciogiordano.travell.adapter.DestinationPresetAdapter;
import com.mauriciogiordano.travell.api.Delegate;
import com.mauriciogiordano.travell.model.Destination;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.widget.HListView;


public class DestinationActivity extends ActionBarActivity {

    private DestinationAdapter adapter;
    private DestinationPresetAdapter presetAdapter = null;
    private View progressBar;
    private ListView listView;
    private View layoutMyItineraries;
    private View layoutSearch;
    private String filter = "";

    private List<Destination> destinationList;
    private List<Destination> destinationListPreset;

    private List<Destination> destinationListBak;
    private List<Destination> destinationListPresetBak;

    private boolean filterApplied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar_background));
        }

        HListView listViewMyItirenaries = (HListView) findViewById(R.id.listViewMyItineraries);
        layoutMyItineraries = findViewById(R.id.layoutMyItineraries);
        layoutSearch = findViewById(R.id.layoutSearch);
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

        destinationList = new ArrayList<>();
        destinationListPreset = Destination.findAll(this);

        if (destinationListPreset.size() > 0) {
            presetAdapter = new DestinationPresetAdapter();

            listViewMyItirenaries.setAdapter(presetAdapter);

            presetAdapter.setDataList(destinationListPreset);

            listViewMyItirenaries.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> adapterView, View view, int i, long l) {
                    Destination destination = (Destination) presetAdapter.getItem(i);

                    Intent intent = new Intent(DestinationActivity.this, ItineraryActivity.class);
                    intent.putExtra("destinationId", destination.getId());
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            layoutMyItineraries.setVisibility(View.GONE);
        }
    }

    private void loadDestinations() {
        progressBar.setVisibility(View.VISIBLE);
        layoutSearch.setVisibility(View.GONE);

        Destination.getTopDestinations(new Delegate() {
            @Override
            public void requestResults(boolean hasInternet, HttpResponse response, JSONObject result) {
                progressBar.setVisibility(View.GONE);
                layoutSearch.setVisibility(View.VISIBLE);

                if (hasInternet) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        try {
                            JSONArray jsonArray = result.getJSONArray("data");

                            destinationList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                Destination d = new Destination(jsonArray.getJSONObject(i), DestinationActivity.this);

                                destinationList.add(d);
                            }

                            adapter.setDataList(destinationList);
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

    private boolean lock = false;

    private void applyFilterFromApi() {
        if (lock) return;
        lock = true;

        progressBar.setVisibility(View.VISIBLE);
        layoutSearch.setVisibility(View.GONE);

        Destination.filter(filter, new Delegate() {
            @Override
            public void requestResults(boolean hasInternet, HttpResponse response, JSONObject result) {
                progressBar.setVisibility(View.GONE);
                layoutSearch.setVisibility(View.VISIBLE);

                if (hasInternet) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        try {
                            JSONArray jsonArray = result.getJSONArray("data");

                            destinationList = new ArrayList<Destination>();

                            for (int i=0; i<jsonArray.length(); i++) {
                                Destination d = new Destination(jsonArray.getJSONObject(i), DestinationActivity.this);

                                destinationList.add(d);
                            }

                            adapter.setDataList(destinationList);
                        } catch (JSONException e) {

                        }
                    }
                }

                lock = false;
            }
        });
    }

    private void applyFilter() {

        if (!filterApplied) {
            destinationListBak = destinationList;
            destinationListPresetBak = destinationListPreset;
        }

        if (filterApplied && filter.equals("")) {
            destinationList = destinationListBak;
            destinationListPreset = destinationListPresetBak;
        } else {

            List<Destination> newDestinationList = new ArrayList<>();

            for (Destination destination : destinationList) {
                if (destination.isSimilar(filter)) {
                    newDestinationList.add(destination);
                }
            }

            List<Destination> newDestinationListPreset = new ArrayList<>();

            for (Destination destination : destinationListPreset) {
                if (destination.isSimilar(filter)) {
                    newDestinationListPreset.add(destination);
                }
            }

            destinationList = newDestinationList;
            destinationListPreset = newDestinationListPreset;

            if (destinationList.size() == 0) {
                applyFilterFromApi();
            }
        }

        adapter.setDataList(destinationList);

        if (presetAdapter != null) {
            presetAdapter.setDataList(destinationListPreset);

            if (destinationListPreset.size() > 0) {
                layoutMyItineraries.setVisibility(View.VISIBLE);
            } else {
                layoutMyItineraries.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflates our menu
        getMenuInflater().inflate(R.menu.destination, menu);

        // Our items
        MenuItem searchItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Our search cancel handler
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                filter = "";
                applyFilter();
                filterApplied = false;

                return false;
            }
        });

        // Our search handler
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String text) {

                applyFilter();
                filterApplied = true;

                return true;
            }

            @Override
            public boolean onQueryTextChange(String text) {

                filter = text;

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


}
