package com.example.guest.apitest.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.guest.apitest.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UpdateDmv extends AppCompatActivity {

    @Bind(R.id.name) TextView mName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_dmv);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");

        mName.setText(name);
    }
}
