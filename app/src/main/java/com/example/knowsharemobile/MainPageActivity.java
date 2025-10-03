package com.example.knowsharemobile;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainPageActivity extends AppCompatActivity {
    private Button btn_verclase;
    private Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.mainpage_activity);

        btn_verclase = findViewById(R.id.btn_verclase);
        btn_logout = findViewById(R.id.btn_logout);


    }
}
