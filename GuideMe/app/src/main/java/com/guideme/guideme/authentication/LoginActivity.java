package com.guideme.guideme.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guideme.guideme.R;
import com.guideme.guideme.home.HomeActivity;
import com.guideme.guideme.model.User;
import com.guideme.guideme.utils.Navigator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 101;

    private TextInputEditText mEmailEditText;
    private TextInputEditText mPasswordEditText;

    private FirebaseAuth mAuth;
    private CollectionReference mUserCollectionRef;
    private ProgressDialog mProgressDialog;
    private GoogleSignInClient mGoogleSignInClient;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailEditText = findViewById(R.id.input_email);
        mPasswordEditText = findViewById(R.id.input_password);
        SignInButton signInButton = findViewById(R.id.sign_in_button);

        mAuth = FirebaseAuth.getInstance();
        mUserCollectionRef = FirebaseFirestore.getInstance().collection("users");

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Authenticating...");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestId()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    public void signUpButtonClick(View view) {
        // Start the Signup activity
        Navigator.navigate(this,
                new Intent(getApplicationContext(), RegisterActivity.class));
    }

    public void onLoginButtonClick(View view) {
        Log.d(TAG, "Login");

        String email = mEmailEditText.getEditableText().toString().trim();
        String password = mPasswordEditText.getEditableText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError("Enter email address!");
            mEmailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError("Enter password!");
            mPasswordEditText.requestFocus();
            return;
        }

        mEmailEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);
        mPasswordEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);
        mProgressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onLoginSuccess(task.getResult().getUser());
                        } else {
                            Log.e(TAG, "Auth error", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });
    }

    private void onLoginSuccess(FirebaseUser firebaseUser) {
        Log.d(TAG, "onLoginSuccess");
        mUserCollectionRef
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        onSuccessfulLogin(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onLoginFailed(e);
                    }
                });

    }

    private void onLoginFailed(Exception e) {
        Log.e(TAG, "Error fetching user data", e);
        mProgressDialog.dismiss();
    }

    private void onSuccessfulLogin(User user) {
        mProgressDialog.dismiss();

        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("user", user);
            Navigator.navigate(LoginActivity.this,
                    intent);
            finish();
        } else {
            Log.d(TAG, "user data not fetched");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            User user = new User();
            user.setId(account.getId());
            user.setName(account.getGivenName());
            user.setEmail(account.getEmail());
            user.setImage(account.getPhotoUrl().toString());
            // Signed in successfully, show authenticated UI.
            onSuccessfulLogin(user);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            onLoginFailed(e);
        }
    }

}

