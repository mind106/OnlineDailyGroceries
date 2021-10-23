package com.rajendra.onlinedailygroceries;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Thanhtoan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanhtoan);

        TextView txtThanhtoan = findViewById(R.id.txtthanhtoan);
        Button btnthanhtoan = findViewById(R.id.btnthanhtoan);
        Intent intent = getIntent();
        txtThanhtoan.setText(intent.getStringExtra("Total"));
        btnthanhtoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Thanhtoan.this, "DAT HANG THANH CONG", Toast.LENGTH_LONG).show();
                Intent  intent1 = new Intent(Thanhtoan.this, MainActivity.class);
                startActivity(intent1);
            }
        });

    }
}