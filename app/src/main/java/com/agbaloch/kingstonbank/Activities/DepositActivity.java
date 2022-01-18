package com.agbaloch.kingstonbank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agbaloch.kingstonbank.Models.Deposit;
import com.agbaloch.kingstonbank.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class DepositActivity extends AppCompatActivity {

    private EditText edtAmount;

    private TextView txtStatus;

    private FirebaseFirestore db;

    private long accountNumber;

    private double balance;

    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        wireUIComponents();

        accountNumber = getIntent().getLongExtra("accountNumber", 0);
        balance = getIntent().getDoubleExtra("balance", 0);
        accountId = getIntent().getStringExtra("accountId");
        db = FirebaseFirestore.getInstance();
    }

    private void wireUIComponents(){

        edtAmount = findViewById(R.id.edt_amount);
        txtStatus = findViewById(R.id.lbl_status);
    }

    public void depositMoney(View view) {

        if(edtAmount.getText().toString().isEmpty()){

            Toast.makeText(this, "Please enter deposit amount", Toast.LENGTH_SHORT).show();
        }

        else{

            final Deposit deposit = new Deposit();
            deposit.setAccountNumber(accountNumber);
            deposit.setAmount(Double.parseDouble(edtAmount.getText().toString()));
            deposit.setDateTime(Timestamp.now());

            db.collection("Transaction")
                    .orderBy("dateTime", Query.Direction.DESCENDING).limit(1)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.getResult().getDocuments().size() > 0){

                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                        deposit.setTransactionId(documentSnapshot.getLong("transactionId") + 1);
                    }

                    else{

                        deposit.setTransactionId(1);
                    }

                    db.collection("Transaction")
                            .add(deposit)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    updateAccountBalance(accountId,balance, deposit.getAmount());
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            txtStatus.setText("Something went wrong");
                            txtStatus.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    txtStatus.setText("Something went wrong");
                    txtStatus.setVisibility(View.VISIBLE);
                }
            });
        }

    }

    private void updateAccountBalance(String accountId, double balance, double amount) {

        double newBalance = balance + amount;

        db.collection("Account").document(accountId)
                .update("balance", newBalance)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        txtStatus.setText("Transaction successful!");
                        txtStatus.setVisibility(View.VISIBLE);
                        edtAmount.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                txtStatus.setText("Something went wrong");
                txtStatus.setVisibility(View.VISIBLE);
            }
        });
    }
}
