package com.agbaloch.kingstonbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.agbaloch.kingstonbank.Activities.LoginActivity;
import com.agbaloch.kingstonbank.Activities.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

// This is the main activity from where the user logins or creates an account
public class MainActivity extends AppCompatActivity {

    private Button btnRegister, btnLogIn;
    private TextView txtAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wireUIComponents();
    }

    //method to link layout(view) elements to activity(controller)
    private void wireUIComponents(){

        btnLogIn = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        txtAdmin = findViewById(R.id.txt_adminLogin);
    }

    public void createAccount(View view) {

        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void logIn(View view) {

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void logInAsAdmin(View view) {
    }
}
