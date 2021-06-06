package com.tutu.hp.howapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class StartActivity extends AppCompatActivity {
    private TextInputEditText UserName;
    private TextInputEditText PhoneNumber;
    private TextInputEditText VerificationCode;

    private Button submitButton;
    private Button signUpButton;

    FirebaseAuth mAuth;
    DatabaseReference reference;

    String codeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //authentication
        mAuth= FirebaseAuth.getInstance();

        UserName= (TextInputEditText) findViewById(R.id.start_Text_Name);
        PhoneNumber= (TextInputEditText) findViewById(R.id.start_Text_Phone);
        VerificationCode= (TextInputEditText) findViewById(R.id.start_Text_Code);
        findViewById(R.id.start_Submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendVerificationCode();

            }
        });
       findViewById(R.id.start_Code_btn).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                 verifySignInCode();
           }
       });
    }
    private void verifySignInCode(){
        String code=VerificationCode.getText().toString().trim();
        if(code.isEmpty()){
            VerificationCode.setError("Verification Code is empty");
            VerificationCode.requestFocus();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String username = UserName.getText().toString().trim();
                            String phoneNumb= PhoneNumber.getText().toString().trim();
                            Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();

                            FirebaseUser firebaseUser= mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String UserId = firebaseUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(UserId);
                             //Register the User is the real time database
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("ID", UserId);
                            hashMap.put("UserName", username);
                            hashMap.put("PhoneNumber", phoneNumb);
                            hashMap.put("ImageURL", "default");
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),"You are now registered",Toast.LENGTH_LONG).show();
                                        Intent intent= new Intent(StartActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Toast.makeText(getApplicationContext(),"Error: Not Registered",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                        } else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            Toast.makeText(getApplicationContext(),"Incorrect Verification",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(){

         String phone= PhoneNumber.getText().toString().trim();
         if(phone.isEmpty()) {
              PhoneNumber.setError("Phone number is required");
              PhoneNumber.requestFocus();
              return;
         }
         if (phone.length() < 10){
             PhoneNumber.setError("Phone number not valid");
             PhoneNumber.requestFocus();
             return;
         }

         PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {


        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(),"Verification Failed",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent=s;
        }
    };
}
