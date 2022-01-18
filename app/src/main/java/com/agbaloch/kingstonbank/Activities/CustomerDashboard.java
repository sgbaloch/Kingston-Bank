package com.agbaloch.kingstonbank.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.agbaloch.kingstonbank.Models.Account;
import com.agbaloch.kingstonbank.Models.Deposit;
import com.agbaloch.kingstonbank.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class CustomerDashboard extends AppCompatActivity {

    private TextView txtUsername, txtAccount, txtBalance;

    private FirebaseFirestore db;

    private String username, customerId, accountId;

    private long accountNumber;

    private double balance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_dashboard);

        wireUIComponents();

        username = getIntent().getStringExtra("username");

        txtUsername.setText("Welcome, " + username);

        db = FirebaseFirestore.getInstance();

        db.collection("Customer").whereEqualTo("username", username)
                .limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

               DocumentSnapshot document = task.getResult().getDocuments().get(0);
               customerId = document.getId();

               db.collection("Account").whereEqualTo("customerId", customerId)
                       .limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
                   @Override
                   public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                       if(value != null){

                           DocumentSnapshot documentSnapshot = value.getDocuments().get(0);

                           accountId = documentSnapshot.getId();
                           accountNumber = documentSnapshot.getLong("accountNumber");
                           balance = documentSnapshot.getDouble("balance");

                           txtAccount.setText("Account Number: " + String.format("%06d", accountNumber));
                           txtBalance.setText("Balance: £" + balance);
                       }
                   }
               });
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(CustomerDashboard.this, "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void wireUIComponents(){

        txtAccount = findViewById(R.id.txt_account);
        txtUsername = findViewById(R.id.txt_username);
        txtBalance = findViewById(R.id.txt_balance);
    }

    public void addMoney(View view) {

        Intent intent = new Intent(CustomerDashboard.this, DepositActivity.class);
        intent.putExtra("accountId", accountId);
        intent.putExtra("accountNumber", accountNumber);
        intent.putExtra("balance", balance);
        startActivity(intent);
    }

    public void withdrawCash(View view) {

        Intent intent = new Intent(CustomerDashboard.this, WithdrawActivity.class);
        intent.putExtra("accountId", accountId);
        intent.putExtra("accountNumber", accountNumber);
        intent.putExtra("balance", balance);
        startActivity(intent);
    }

    public void viewStatement(View view) {
    }

    public void transferFunds(View view) {
    }
}
