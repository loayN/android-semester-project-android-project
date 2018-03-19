package a2lend.app.com.a2lend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class Register extends AppCompatActivity implements View.OnClickListener {
    // UI references.
    private EditText  emailEditText,passwordEditText,repPasswordEditText;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.emailR);
        passwordEditText = findViewById(R.id.passwordR);
        repPasswordEditText = findViewById(R.id.repPasswordR);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        findViewById(R.id.signupR).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String repPassword = repPasswordEditText.getText().toString();

        //region Validation
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
        if(repPassword.isEmpty()) {
            repPasswordEditText.setError("password is required");
            repPasswordEditText.requestFocus();
            return;
        }
        if(!repPassword.equals(password)) {
            passwordEditText.setError("password is not equal");
            passwordEditText.setText("");
            repPasswordEditText.setText("");
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

        //region Crete New User Firebase
        progressBar.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "createUserWithEmail:success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Register.this ,HomePageActivity.class);
                           // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else{
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "createUserWithEmail:failure\n"
                                        + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
        //endregion

    }
}
