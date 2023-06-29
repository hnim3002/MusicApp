package com.example.btl_app_music.Fragment.BottomNavFragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.btl_app_music.LoginHandle.Login;

import com.example.btl_app_music.MainActivity;
import com.example.btl_app_music.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountInfo extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView userEmail;

    private Button signOutBtn;

    private LinearLayout avatarChange, emailChange, passwordChange;

    private CircleImageView userAvatar;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        openGallery();
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent == null) {
                            return;
                        }

                        Uri uri = intent.getData();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build();

                        assert user != null;
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Glide.with(requireActivity()).load(user.getPhotoUrl()).error(R.drawable.default_avatar).into(userAvatar);
                                        }
                                    }
                                });
                    }
                }
            });




    public AccountInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountInfo newInstance(String param1, String param2) {
        AccountInfo fragment = new AccountInfo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userAvatar = requireView().findViewById(R.id.userAvatar);

        avatarChange = requireView().findViewById(R.id.avatarChangeBtn);
        emailChange = requireView().findViewById(R.id.emailChangeBtn);
        passwordChange = requireView().findViewById(R.id.passwordChangeBtn);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = requireView().findViewById(R.id.userEmail);
        assert user != null;
        userEmail.setText(Objects.requireNonNull(user.getEmail()));
        Glide.with(requireActivity()).load(user.getPhotoUrl()).error(R.drawable.default_avatar).into(userAvatar);

        signOutBtn = requireView().findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        avatarChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });

        emailChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEmailChangeDialog();
            }
        });

        passwordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPasswordChangeDialog();
            }
        });



    }

    public void onClickRequestPermission() {

        if(requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();


        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }



    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Pick Picture"));
    }

    public void openEmailChangeDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_email_dialog);


        CardView cancelBtn = dialog.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        TextInputEditText inputDialog = dialog.findViewById(R.id.editTextDialog);
        TextInputEditText inputDialog2 = dialog.findViewById(R.id.editTextDialog2);

        TextInputLayout emailLayout = dialog.findViewById(R.id.emailLayout);
        TextInputLayout emailLayout2 = dialog.findViewById(R.id.emailLayout2);
        CardView confirmButton = dialog.findViewById(R.id.confirm_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = Objects.requireNonNull(inputDialog.getText()).toString().trim();
                String email2 = Objects.requireNonNull(inputDialog2.getText()).toString().trim();

                if(TextUtils.isEmpty(email)) {

                    emailLayout.setErrorEnabled(true);
                    emailLayout.setError("Please enter your email!");
                    return;
                }
                if(TextUtils.isEmpty(email2)) {

                    emailLayout2.setErrorEnabled(true);
                    emailLayout2.setError("Please enter your password!");
                    return;
                }

                if(inputDialog.getText().toString().trim().equals(user.getEmail())) {

                    user.updateEmail(email2)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                    } else {
                                        openReSiginDialog();
                                    }
                                }
                            });
                }
                else {
                    emailLayout.setErrorEnabled(true);
                    emailLayout.setError("Wrong Email");
                }
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    public void openPasswordChangeDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_password_dialog);


        CardView cancelBtn = dialog.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        TextInputEditText inputDialog = dialog.findViewById(R.id.editTextDialog);
        TextInputEditText inputDialog2 = dialog.findViewById(R.id.editTextDialog2);
        TextInputEditText inputDialog3 = dialog.findViewById(R.id.editTextDialog3);

        TextInputLayout emailLayout = dialog.findViewById(R.id.emailLayout);
        TextInputLayout emailLayout2 = dialog.findViewById(R.id.emailLayout2);
        TextInputLayout emailLayout3 = dialog.findViewById(R.id.emailLayout3);
        CardView confirmButton = dialog.findViewById(R.id.confirm_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String passwordNew = Objects.requireNonNull(inputDialog2.getText()).toString().trim();
                String passwordNew2 = Objects.requireNonNull(inputDialog3.getText()).toString().trim();


                if(TextUtils.isEmpty(passwordNew)) {

                    emailLayout2.setErrorEnabled(true);
                    emailLayout2.setError("Please enter your password!");
                    return;
                }

                if(TextUtils.isEmpty(passwordNew2)) {

                    emailLayout2.setErrorEnabled(true);
                    emailLayout2.setError("Please enter your password!");
                    return;
                }

                if(inputDialog2.getText().toString().trim().equals(inputDialog3.getText().toString().trim())) {
                    String newPassword = inputDialog2.getText().toString().trim();

                    assert user != null;
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                    } else {
                                        openReSiginDialog();
                                    }
                                }
                            });
                }
                else {

                    emailLayout3.setErrorEnabled(true);
                    emailLayout3.setError("Wrong Password");
                }
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    public void openReSiginDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.create_playlist_dialog);

        TextInputEditText editTextEmail = dialog.findViewById(R.id.Email);
        TextInputEditText editTextPassword = dialog.findViewById(R.id.Password);


        TextInputLayout  emailInputLayout = dialog.findViewById(R.id.emailLayout);
        TextInputLayout passwordInputLayout = dialog.findViewById(R.id.passwordLayout);

        CardView confirmButton = dialog.findViewById(R.id.btn_login);

        String email, password;
        email = String.valueOf(editTextEmail.getText());
        password = String.valueOf(editTextPassword.getText());

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(email)) {

                    emailInputLayout.setErrorEnabled(true);
                    emailInputLayout.setError("Please enter your email!");
                    return;
                }
                if(TextUtils.isEmpty(password)) {

                    passwordInputLayout.setErrorEnabled(true);
                    passwordInputLayout.setError("Please enter your password!");
                    return;
                }

                reAuthentication(email, password);
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    public void reAuthentication(String email, String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);

        // Prompt the user to re-provide their sign-in credentials
        assert user != null;
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

}