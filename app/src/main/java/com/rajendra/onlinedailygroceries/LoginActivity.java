package com.rajendra.onlinedailygroceries;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    ImageView back3;
    EditText username, password;
    Button btnLogin;
    DBHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.usernameLogin);
        password = (EditText) findViewById(R.id.passwordLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        myDB = new DBHelper(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if (user.equals("") || pass.equals("")) {

                    Toast.makeText(LoginActivity.this, "Please enter the Credentials.", Toast.LENGTH_SHORT).show();

                } else {

                    Boolean result = myDB.checkusernamePassword(user, pass);

                    if (result == true) {

                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.putExtra("username", user);
                        startActivity(i);

                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Crediantials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        back3 = findViewById(R.id.back3);

        back3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent back3 = new Intent(LoginActivity.this, Login_Register.class);
                startActivity(back3);
                finish();
            }
        });

    }
}