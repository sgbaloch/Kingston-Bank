package com.agbaloch.kingstonbank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.agbaloch.kingstonbank.Models.Customer;
import com.agbaloch.kingstonbank.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        wireUIComponents();

        db = FirebaseFirestore.getInstance();

    }

    private void wireUIComponents(){

        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
    }


    // This method verifies the username and password from firestore database,
    // if successful it will take the customer to customer dashboard screen.
    public void logIn(View view) {

        db.collection("Customer")
                .whereEqualTo("username", edtUsername.getText().toString())
                .whereEqualTo("password", edtPassword.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){

                            if(!task.getResult().isEmpty()){

                                Intent intent = new Intent(LoginActivity.this,
                                        CustomerDashboard.class);

                                intent.putExtra("username", edtUsername.getText().toString());
                                startActivity(intent);

                            }

                            else{

                                Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
