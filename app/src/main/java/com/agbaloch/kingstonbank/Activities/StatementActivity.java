package com.agbaloch.kingstonbank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.agbaloch.kingstonbank.Adapters.StatementAdapter;
import com.agbaloch.kingstonbank.Models.Transaction;
import com.agbaloch.kingstonbank.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StatementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    private TextView txtAccount, txtTitle, txtAccType;

    private List<Transaction> transactionList;

    private FirebaseFirestore db;

    private long accountNumber;

    private double balance;

    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

        // Getting the data passed by previous screen
        accountNumber = getIntent().getLongExtra("accountNumber", 0);
        balance = getIntent().getDoubleExtra("balance", 0);
        accountId = getIntent().getStringExtra("accountId");

        // linking UI components by using ids
        recyclerView = findViewById(R.id.recycler_statement);
        txtAccount = findViewById(R.id.txt_account);
        txtTitle = findViewById(R.id.txt_title);
        txtAccType = findViewById(R.id.txt_acc_type);

        String accNum = String.format("%06d", accountNumber);

        txtAccount.setText("A/C No.: " + accNum);

        // Layout manager for Recycler view (List view)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        transactionList = new ArrayList<>();

        // getting an instance of database
        db = FirebaseFirestore.getInstance();

        // Getting account data from database and populating list of transactions
        // and Header views
        db.collection("Account").document(accountId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){

                            DocumentSnapshot documentSnapshot = task.getResult();

                            long accType;

                            accType = documentSnapshot.getLong("accountType");

                            if(accType == 1){

                                txtAccType.setText("Current Account");
                            }
                            else if(accType == 2){

                                txtAccType.setText("SavingAccount");
                            }

                            String customerId = documentSnapshot.getString("customerId");

                            db.collection("Customer").document(customerId)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if(task.isSuccessful()){

                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                String firstName = documentSnapshot.getString("firstName");
                                                String lastName = documentSnapshot.getString("lastName");

                                                txtTitle.setText("Title: " + firstName + " " + lastName);
                                            }
                                        }
                                    });

                        }
                    }
                });

        db.collection("Transaction")
                .whereEqualTo("accountNumber", accountNumber)
                .orderBy("dateTime", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Transaction transaction = document.toObject(Transaction.class);
                                transactionList.add(transaction);

                            }

                            mAdapter = new StatementAdapter(transactionList);
                            recyclerView.setAdapter(mAdapter);
                        }
                    }
                });
    }
}
