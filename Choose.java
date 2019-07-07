package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Choose extends AppCompatActivity implements View.OnClickListener{

    Button b1,b2;
    TextView textView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose);

        b1= (Button)findViewById(R.id.google);
        b2= (Button)findViewById(R.id.arc);
        textView= (TextView)findViewById(R.id.text);

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google:
                Toast.makeText(this, "Google Maps", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Choose.this, Google.class);
                startActivity(intent);
                finish();
                break;
            case R.id.arc:
                Toast.makeText(this, "Arc GIS Maps", Toast.LENGTH_SHORT).show();
                intent = new Intent(Choose.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
    }
}
}

