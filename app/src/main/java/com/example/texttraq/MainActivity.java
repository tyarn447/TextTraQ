package com.example.texttraq;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }//this is a comment

    Intent intent = new Intent(this, activity_contacts.class);

    public void goToContacts(View view) {
        startActivity(intent);
    }
}
