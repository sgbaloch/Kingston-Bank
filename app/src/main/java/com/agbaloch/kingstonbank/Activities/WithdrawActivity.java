package com.agbaloch.kingstonbank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agbaloch.kingstonbank.Models.Withdraw;
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

public class WithdrawActivity extends AppCompatActivity {

    private EditText edtAmount;

    private TextView txtStatus;

    private FirebaseFirestore db;

    private long accountNumber;

    private double balance;

    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

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

    public void withdrawMoney(View view) {

        String withdrawAmount = edtAmount.getText().toString();

        if(withdrawAmount.isEmpty()){

            Toast.makeText(this, "Please enter deposit amount", Toast.LENGTH_SHORT).show();
        }

        else if(Double.parseDouble(withdrawAmount) > balance){

            Toast.makeText(this, "You don't have enough funds", Toast.LENGTH_LONG).show();
        }

        else {

            final Withdraw withdraw = new Withdraw();
            withdraw.setAccountNumber(accountNumber);
            withdraw.setAmount(Double.parseDouble(withdrawAmount));
            withdraw.setDateTime(Timestamp.now());

            db.collection("Transaction")
                    .orderBy("dateTime", Query.Direction.DESCENDING).limit(1)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.getResult().getDocuments().size() > 0){

                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                        withdraw.setTransactionId(documentSnapshot.getLong("transactionId") + 1);
                    }

                    else{

                        withdraw.setTransactionId(1);
                    }

                    db.collection("Transaction")
                            .add(withdraw)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    updateAccountBalance(accountId,balance, withdraw.getAmount());
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

        double newBalance = balance - amount;

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
