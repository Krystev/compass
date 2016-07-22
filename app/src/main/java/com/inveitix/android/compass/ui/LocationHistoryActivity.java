package com.inveitix.android.compass.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.inveitix.android.compass.ui.adapter.LocationsAdapter;
import com.inveitix.android.compass.R;
import com.inveitix.android.compass.database.adapters.LocationDbAdapter;
import com.inveitix.android.compass.database.models.LocationModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationHistoryActivity extends AppCompatActivity {

    @BindView(R.id.rec_view)
    RecyclerView recView;
    private LocationsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);
        ButterKnife.bind(this);
        adapter = new LocationsAdapter(this);
        adapter.setListener(listener);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(adapter);
        loadData();
    }

    private void loadData() {
        new AsyncTask<Void, Void, List<LocationModel>>() {

            @Override
            protected List<LocationModel> doInBackground(Void... params) {
                return new LocationDbAdapter(LocationHistoryActivity.this).findAll(null);
            }

            @Override
            protected void onPostExecute(List<LocationModel> locationModels) {
                super.onPostExecute(locationModels);
                if(locationModels != null) {
                    adapter.setData(locationModels);
                }
            }
        }.execute();
    }

    LocationsAdapter.OnItemChangedListener listener = new LocationsAdapter.OnItemChangedListener() {
        @Override
        public void onItemRemoved(final LocationModel item) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    new LocationDbAdapter(LocationHistoryActivity.this).delete(item.getId());
                    return null;
                }
            }.execute();
        }
    };
}
