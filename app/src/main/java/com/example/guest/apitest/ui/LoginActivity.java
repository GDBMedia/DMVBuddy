package com.example.guest.apitest.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.guest.apitest.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends Activity implements View.OnClickListener {
    @Bind(R.id.submit) Button mSubmit;
    @Bind(R.id.username) EditText mUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mSubmit) {
            String username = mUsername.getText().toString();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        }

    }
}
