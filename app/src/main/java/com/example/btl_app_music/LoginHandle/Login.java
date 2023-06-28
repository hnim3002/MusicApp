package com.example.btl_app_music.LoginHandle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
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
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private static final String FILE_NAME = "account";
    private TextView textLogin;
    private ProgressBar progressBar;
    private CardView loginBtn;
    private TextInputEditText editTextEmail, editTextPassword;
    private CheckBox rememberCk;
    private TextView toRegister, toForgotPassword;

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
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        loginBtn = findViewById(R.id.btn_login);
        editTextEmail = findViewById(R.id.Email);
        editTextPassword = findViewById(R.id.Password);
        rememberCk = findViewById(R.id.ckRemember);
        toRegister = findViewById(R.id.to_register);
        progressBar = findViewById(R.id.progressBarLog);
        textLogin = findViewById(R.id.textLogin);
        toForgotPassword = findViewById(R.id.toForgotPassword);


        toForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");

        editTextEmail.setText(email);
        editTextPassword.setText(password);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if(rememberCk.isChecked()) {
                    StorageInfoLogin(email, password);
                } else {
                    DeleteInfoLogin();
                }


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

                progressBar.setVisibility(View.VISIBLE);
                textLogin.setVisibility(View.GONE);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                textLogin.setVisibility(View.VISIBLE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(Login.this, "Authentication succes.",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.

                                    TextInputLayout emailInputLayout = findViewById(R.id.emailLayout);
                                    emailInputLayout.setErrorEnabled(true);
                                    emailInputLayout.setError("Wrong email or password");
                                    TextInputLayout passwordInputLayout = findViewById(R.id.passwordLayout);
                                    passwordInputLayout.setErrorEnabled(true);
                                    passwordInputLayout.setError("");

                                }
                            }
                        });
            }
        });
    }
    public void StorageInfoLogin(String email,String password) {
        SharedPreferences.Editor editor = getSharedPreferences(FILE_NAME,MODE_PRIVATE).edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    public void DeleteInfoLogin() {
        SharedPreferences setting = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        setting.edit().clear().apply();
    }

}