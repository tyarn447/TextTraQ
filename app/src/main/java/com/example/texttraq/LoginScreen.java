package com.example.texttraq;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
    }

    public void launchCreateAccount(View view){
        Intent intent = new Intent(this,CreateNewAccount.class);
        startActivity(intent);

    }
}
