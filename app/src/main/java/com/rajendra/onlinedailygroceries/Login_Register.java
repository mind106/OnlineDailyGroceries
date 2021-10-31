package com.rajendra.onlinedailygroceries;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Login_Register extends AppCompatActivity {

    ImageView back;
    EditText username, password, repassword;
    Button btnSignUp, btnSignIn;
    DBHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        repassword = (EditText)findViewById(R.id.repassword);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        myDB = new DBHelper(this);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            String user = username.getText().toString();
            String pass = password.getText().toString();
            String repass = repassword.getText().toString();

                if (user.equals("") || pass.equals("") || repass.equals("")) {
                    Toast.makeText(Login_Register.this, "Fill all the fields.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(pass.equals(repass)){
                        Boolean usercheckResult = myDB.checkusername(user);
                        if(usercheckResult == false){
                           Boolean reResult = myDB.insertData(user,pass);
                            if (reResult == true) {
                                Toast.makeText(Login_Register.this, "Registrantion Seccessful.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login_Register.this, LoginActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(Login_Register.this, "Registrantion Failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(Login_Register.this, "User already Exits.\nPlease Sign In", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(Login_Register.this, "Password not Matching.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_Register.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent back = new Intent(Login_Register.this , MainActivity.class);
                startActivity(back);
                finish();
            }
        });
    }
}