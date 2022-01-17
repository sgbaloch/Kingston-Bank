package com.agbaloch.kingstonbank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.agbaloch.kingstonbank.Models.Account;
import com.agbaloch.kingstonbank.Models.CurrentAccount;
import com.agbaloch.kingstonbank.Models.Customer;
import com.agbaloch.kingstonbank.Models.SavingAccount;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private Button btnCreateAccount;

    @NotEmpty()
    private EditText edtFName;

    @NotEmpty
    private EditText edtLName;

    @NotEmpty
    @Email
    private EditText edtEmail;

    @NotEmpty
    private EditText edtAddress;

    @NotEmpty
    private EditText edtUsername;

    @NotEmpty
    @Password(min = 4)
    private EditText edtPassword;

    @ConfirmPassword
    private EditText edtCPassword;

    RadioGroup radioAccType;
    RadioButton rbCurrent, rbSaving;

    private Validator validator;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        wireUIComponents();

        validator = new Validator(this);
        db = FirebaseFirestore.getInstance();

        validator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {

                if(rbCurrent.isChecked() || rbSaving.isChecked()){

                    addCustomerIfNotAlreadyRegistered(edtUsername.getText().toString());
                }
                else{

                    Toast.makeText(RegisterActivity.this, "Please select account type"
                            , Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {

                for (ValidationError error : errors) {
                    View view = error.getView();
                    String message = error.getCollatedErrorMessage(RegisterActivity.this);

                    // Display error messages ;)
                    if (view instanceof EditText) {
                        ((EditText) view).setError(message);
                    } else {
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    private void addCustomerIfNotAlreadyRegistered(String username) {

        db.collection("Customer")
                .whereEqualTo("UserName", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.getResult().size() == 0){

                            createAccount();
                        }
                        else{

                            Toast.makeText(RegisterActivity.this, "Username already exist!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createAccount() {

        final Customer customer = new Customer();
        customer.setFirstName(edtFName.getText().toString());
        customer.setLastName(edtLName.getText().toString());
        customer.setUsername(edtUsername.getText().toString());
        customer.setEmail(edtEmail.getText().toString());
        customer.setAddress(edtAddress.getText().toString());
        customer.setPassword(edtPassword.getText().toString());

        db.collection("Customer")
                .add(customer)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        String customerId = documentReference.getId();
                        int accType;

                        if(radioAccType.getCheckedRadioButtonId() == rbCurrent.getId()){

                            accType = 1;
                        }
                        else {

                            accType = 2;
                        }

                        requestNewAccount(accType, customerId);

                        Toast.makeText(RegisterActivity.this, "Account created successfully",
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void requestNewAccount(final int accType, final String customerId) {

        db.collection("Account").orderBy("DateCreated", Query.Direction.DESCENDING)
                .limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                long accountNumber = document.getLong("AccountNumber");
                accountNumber++;

                final Account account;

                if(accType == 1){

                    account = new CurrentAccount();
                }
                else{

                    account = new SavingAccount();
                }

                account.setAccountNumber(accountNumber);
                account.setBalance(0);
                account.setCustomerId(customerId);
                account.setDateCreated(Timestamp.now());

                db.collection("Account").add(account).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        String accNo = String.format("%08d", account.getAccountNumber());
                        Toast.makeText(RegisterActivity.this, "Your account no. is "
                                + accNo, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void wireUIComponents(){

        edtEmail = findViewById(R.id.edt_email);
        edtFName = findViewById(R.id.edt_fName);
        edtLName = findViewById(R.id.edt_lName);
        edtUsername = findViewById(R.id.edt_username);
        edtAddress = findViewById(R.id.edt_address);
        edtPassword = findViewById(R.id.edt_pass);
        edtCPassword = findViewById(R.id.edt_cPass);
        btnCreateAccount = findViewById(R.id.btn_createAccount);

        radioAccType = findViewById(R.id.rg_accType);
        rbCurrent = findViewById(R.id.rb_current);
        rbSaving = findViewById(R.id.rb_saving);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validator.validate();
            }
        });
    }
}
