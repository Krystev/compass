package com.inveitix.android.compass;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationHistoryActivity extends AppCompatActivity {

    @BindView(R.id.rec_view)
    RecyclerView recView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);
        ButterKnife.bind(this);

        loadData();
    }
}
