package a2lend.app.com.a2lend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.print.pdf.PrintedPdfDocument;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class MainActivity extends AppCompatActivity {
    private EditText emailEditText,passwordEditText;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER); // default 0

        mAuth = FirebaseAuth.getInstance();
    }


    public void signin(final View view) {



        //region Check Validation
        final String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }
        if(password.isEmpty()) {
            passwordEditText.setError("password is required");
            passwordEditText.requestFocus();
            return;
        }
        if(password.isEmpty()) {
            passwordEditText.setError("password is required");
            passwordEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please enter a valid Email");
            emailEditText.requestFocus();
            return;
        }
        if(password.length()<6){
            passwordEditText.setError("Please enter a long Password ");
            passwordEditText.requestFocus();
            return;
        }
        //endregion
        progressDialog.setTitle("Login ...");
        progressDialog.setMessage("Wait .. ");
        progressDialog.show();
        //region Login FireBase With Email Password
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Toast.makeText(getApplicationContext(), "SinginUser:success", Toast.LENGTH_SHORT).show();

                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    // Clear TextBoxes
                    emailEditText.setText(""); passwordEditText.setText("");
                    // dismiss progressDialog
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });

                    // Login -> To Home Page
                    Intent intent = new Intent(MainActivity.this ,HomePageActivity.class);
                    startActivity(intent);
                }else{
                    if(task.getException() instanceof FirebaseAuthInvalidUserException)
                         Toast.makeText(getApplicationContext(), "SinginUser:failure\n"
                                        + "Invalid Email Exception", Toast.LENGTH_LONG).show();
                    else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                         Toast.makeText(getApplicationContext(), "SinginUser:failure\n"
                                        + "Invalid Credentials Exception", Toast.LENGTH_LONG).show();
                    else Toast.makeText(getApplicationContext(), "SinginUser:failure\n"
                                        + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
        //endregion
    }

    public void signInAnonymously(View view) {

        progressDialog.setTitle("Login ...");
        progressDialog.setMessage("sign In Anonymously");
        progressDialog.show();

        //region Login FireBase With User Anonymously
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d("signIn", "signInAnonymously:SUCCESS");
                Toast.makeText(getApplicationContext(), "SignIn Anonymously:success", Toast.LENGTH_SHORT).show();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() { progressDialog.dismiss();}
                });
                if(authResult!= null) {
                    // Login -> To Home Page
                    Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                    startActivity(intent);
                }
                return;
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        Log.e("signIn", "signInAnonymously:FAILURE", exception);
                        Toast.makeText(getApplicationContext(), "signIn Anonymously:FAILURE", Toast.LENGTH_SHORT).show();
                        MySupport.showMessageDialog(MainActivity.this,"signIn:FAILURE",exception.getMessage());

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() { progressDialog.dismiss(); }
                        });
                    }
                });

        //endregion

    }

    public void forgotPassword(View view) {
        if(emailEditText.getText().toString().isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches()){
            emailEditText.setError("Please enter a valid Email");
            emailEditText.requestFocus();
            return;
        }
        DataAccess.sendPasswordResetEmail(this,emailEditText.getText().toString());
    }

    public void register(View view){
        startActivity(new Intent(this,Register.class));
    }



}