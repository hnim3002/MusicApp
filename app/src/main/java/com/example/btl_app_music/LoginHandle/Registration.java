package com.example.btl_app_music.LoginHandle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl_app_music.MainActivity;
import com.example.btl_app_music.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class Registration extends AppCompatActivity {

    private TextView textLogin;
    private ProgressBar progressBar;
    private TextInputEditText editTextEmail, editTextPassword, getEditTextPasswordRepeat;

    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        TextView toLogin = findViewById(R.id.to_login);
        CardView registerBtn = findViewById(R.id.btn_register);
        editTextEmail = findViewById(R.id.Email);
        editTextPassword = findViewById(R.id.Password);
        getEditTextPasswordRepeat = findViewById(R.id.Password_repeat);
        progressBar = findViewById(R.id.progressBarLog);
        textLogin = findViewById(R.id.textLogin);


        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, Login.class);
                startActivity(intent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, password2;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                password2 = String.valueOf(getEditTextPasswordRepeat.getText());

                if(TextUtils.isEmpty(email)) {
                    TextInputLayout emailInputLayout = findViewById(R.id.emailLayout);
                    emailInputLayout.setErrorEnabled(true);
                    emailInputLayout.setError("Please enter your email!");
                    return;
                }
                if(TextUtils.isEmpty(password)) {
                    TextInputLayout passwordInputLayout = findViewById(R.id.passwordLayout);
                    passwordInputLayout.setErrorEnabled(true);
                    passwordInputLayout.setError("Please enter your password!");
                    return;
                }
                if(TextUtils.isEmpty(password2)) {
                    TextInputLayout repeatPasswordInputLayout = findViewById(R.id.repeatPasswordLayout);
                    repeatPasswordInputLayout.setErrorEnabled(true);
                    repeatPasswordInputLayout.setError("Please enter your password!");
                    return;
                }
                if(!password2.equals(password)) {
                    TextInputLayout repeatPasswordInputLayout = findViewById(R.id.repeatPasswordLayout);
                    repeatPasswordInputLayout.setErrorEnabled(true);
                    repeatPasswordInputLayout.setError("Password is not corect!");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                textLogin.setVisibility(View.GONE);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                textLogin.setVisibility(View.VISIBLE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(Registration.this, "Authentication succes.",
                                            Toast.LENGTH_SHORT).show();
                                    //FirebaseUser user = mAuth.getCurrentUser();

                                } else {
                                    // If sign in fails, display a message to the user.

                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        TextInputLayout emailInputLayout = findViewById(R.id.emailLayout);
                                        emailInputLayout.setErrorEnabled(true);
                                        emailInputLayout.setError("User with this email already exist");
                                        return;
                                    }
                                    Toast.makeText(Registration.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}