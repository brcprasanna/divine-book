package ram.king.com.divinebook.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ram.king.com.divinebook.R;
import ram.king.com.divinebook.models.User;
import ram.king.com.divinebook.util.AppConstants;
import ram.king.com.divinebook.util.AppUtil;

public class SignInActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "SignInActivity";

    private static final int RC_SIGN_IN = 9001;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
/*
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSignInButton;
    private Button mSignUpButton;
*/

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        // Views
/*
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        mSignInButton = (Button) findViewById(R.id.button_sign_in);
        mSignUpButton = (Button) findViewById(R.id.button_sign_up);


        // Click listeners
        mSignInButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
*/

        findViewById(R.id.button_gmail_sign_in).setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        FirebaseAnalytics.getInstance(this);
        //AppUtil.getDynamicLink(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

/*
    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
*/

    private void onAuthSuccess(FirebaseUser user) {
        String displayName;
        String photoUrl;

        if (user != null) {
            String username = usernameFromEmail(user.getEmail());
            String moderatorFlag;

            if (user.getEmail().equals(AppConstants.VISHNU_MANTRAS_EMAIL) ||
                    user.getEmail().equals(AppConstants.SHIVA_MANTRAS_EMAIL) ||
                    user.getEmail().equals(AppConstants.GANAPATHY_MANTRAS_EMAIL) ||
                    user.getEmail().equals(AppConstants.SAI_BABA_MANTRAS_EMAIL) ||
                    user.getEmail().equals(AppConstants.KRISHNA_MANTRAS_EMAIL) ||
                    user.getEmail().equals(AppConstants.RAMANUJAR_MANTRAS_EMAIL) ||
                    user.getEmail().equals(AppConstants.LAKSHMI_MANTRAS_EMAIL) ||
                    user.getEmail().equals(AppConstants.UPANISHAD_EMAIL) ||
                    user.getEmail().equals(AppConstants.PURANAS_STORY_EMAIL) ||
                    user.getEmail().equals(AppConstants.HANUMAN_MANTRAS_EMAIL)) {
                moderatorFlag = "1E";
                AppUtil.putBoolean(SignInActivity.this, AppConstants.ADMIN_USER, true);
                AppUtil.putString(SignInActivity.this, AppConstants.MODERATOR_FLAG, "1E");

                if (user.getEmail().equals(AppConstants.VISHNU_MANTRAS_EMAIL)) {
                    displayName = AppConstants.VISHNU_MANTRAS_NAME;
                    photoUrl = AppConstants.VISHNU_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.SHIVA_MANTRAS_EMAIL)) {
                    displayName = AppConstants.SHIVA_MANTRAS_NAME;
                    photoUrl = AppConstants.SHIVA_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.GANAPATHY_MANTRAS_EMAIL)) {
                    displayName = AppConstants.GANAPATHY_MANTRAS_NAME;
                    photoUrl = AppConstants.GANAPATHY_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.SAI_BABA_MANTRAS_EMAIL)) {
                    displayName = AppConstants.SAI_BABA_MANTRAS_NAME;
                    photoUrl = AppConstants.SAIBABA_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.KRISHNA_MANTRAS_EMAIL)) {
                    displayName = AppConstants.KRISHNA_MANTRAS_NAME;
                    photoUrl = AppConstants.KRISHNA_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.RAMANUJAR_MANTRAS_EMAIL)) {
                    displayName = AppConstants.RAMANUJAR_MANTRAS_NAME;
                    photoUrl = AppConstants.RAMANUJAR_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.LAKSHMI_MANTRAS_EMAIL)) {
                    displayName = AppConstants.LAKSHMI_MANTRAS_NAME;
                    photoUrl = AppConstants.LAKSHMI_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.UPANISHAD_EMAIL)) {
                    displayName = AppConstants.UPANISHAD_NAME;
                    photoUrl = AppConstants.UPANISHAD_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.PURANAS_STORY_EMAIL)) {
                    displayName = AppConstants.PURANAS_STORY_NAME;
                    photoUrl = AppConstants.PURANAS_STORY_IMAGE;
                } else if (user.getEmail().equals(AppConstants.HANUMAN_MANTRAS_EMAIL)) {
                    displayName = AppConstants.HANUMAN_MANTRAS_NAME;
                    photoUrl = AppConstants.HANUMAN_MANTRAS_IMAGE;
                } else {
                    displayName = user.getDisplayName();
                    photoUrl = user.getPhotoUrl().toString();
                }

            } else if (user.getEmail().equals(AppConstants.VISHNU_MANTRAS_EMAIL_TAMIL) ||
                    user.getEmail().equals(AppConstants.SHIVA_MANTRAS_EMAIL_TAMIL) ||
                    user.getEmail().equals(AppConstants.GANAPATHY_MANTRAS_EMAIL_TAMIL) ||
                    user.getEmail().equals(AppConstants.SAI_BABA_MANTRAS_EMAIL_TAMIL) ||
                    user.getEmail().equals(AppConstants.KRISHNA_MANTRAS_EMAIL_TAMIL) ||
                    user.getEmail().equals(AppConstants.RAMANUJAR_MANTRAS_EMAIL_TAMIL) ||
                    user.getEmail().equals(AppConstants.LAKSHMI_MANTRAS_EMAIL_TAMIL) ||
                    user.getEmail().equals(AppConstants.UPANISHAD_EMAIL_TAMIL) ||
                    user.getEmail().equals(AppConstants.MURUGAN_MANTRAS_EMAIL_TAMIL) ||
                    user.getEmail().equals(AppConstants.PURANAS_STORY_EMAIL_TAMIL) ||
                    user.getEmail().equals(AppConstants.HANUMAN_MANTRAS_EMAIL_TAMIL)) {
                moderatorFlag = "1T";
                AppUtil.putBoolean(SignInActivity.this, AppConstants.ADMIN_USER, true);
                AppUtil.putString(SignInActivity.this, AppConstants.MODERATOR_FLAG, "1T");


                if (user.getEmail().equals(AppConstants.VISHNU_MANTRAS_EMAIL_TAMIL)) {
                    displayName = AppConstants.VISHNU_MANTRAS_NAME_TAMIL;
                    photoUrl = AppConstants.VISHNU_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.SHIVA_MANTRAS_EMAIL_TAMIL)) {
                    displayName = AppConstants.SHIVA_MANTRAS_NAME_TAMIL;
                    photoUrl = AppConstants.SHIVA_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.GANAPATHY_MANTRAS_EMAIL_TAMIL)) {
                    displayName = AppConstants.GANAPATHY_MANTRAS_NAME_TAMIL;
                    photoUrl = AppConstants.GANAPATHY_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.SAI_BABA_MANTRAS_EMAIL_TAMIL)) {
                    displayName = AppConstants.SAI_BABA_MANTRAS_NAME_TAMIL;
                    photoUrl = AppConstants.SAIBABA_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.KRISHNA_MANTRAS_EMAIL_TAMIL)) {
                    displayName = AppConstants.KRISHNA_MANTRAS_NAME_TAMIL;
                    photoUrl = AppConstants.KRISHNA_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.RAMANUJAR_MANTRAS_EMAIL_TAMIL)) {
                    displayName = AppConstants.RAMANUJAR_MANTRAS_NAME_TAMIL;
                    photoUrl = AppConstants.RAMANUJAR_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.LAKSHMI_MANTRAS_EMAIL_TAMIL)) {
                    displayName = AppConstants.LAKSHMI_MANTRAS_NAME_TAMIL;
                    photoUrl = AppConstants.LAKSHMI_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.UPANISHAD_EMAIL_TAMIL)) {
                    displayName = AppConstants.UPANISHAD_NAME_TAMIL;
                    photoUrl = AppConstants.UPANISHAD_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.MURUGAN_MANTRAS_EMAIL_TAMIL)) {
                    displayName = AppConstants.MURUGAN_MANTRAS_NAME_TAMIL;
                    photoUrl = AppConstants.MURUGAN_MANTRAS_IMAGE;
                } else if (user.getEmail().equals(AppConstants.PURANAS_STORY_EMAIL_TAMIL)) {
                    displayName = AppConstants.PURANAS_STORY_NAME_TAMIL;
                    photoUrl = AppConstants.PURANAS_STORY_IMAGE;
                } else if (user.getEmail().equals(AppConstants.HANUMAN_MANTRAS_EMAIL_TAMIL)) {
                    displayName = AppConstants.HANUMAN_MANTRAS_NAME_TAMIL;
                    photoUrl = AppConstants.HANUMAN_MANTRAS_IMAGE;
                } else {
                    displayName = user.getDisplayName();
                    photoUrl = user.getPhotoUrl().toString();
                }
            } else {
                moderatorFlag = "0";
                AppUtil.putBoolean(SignInActivity.this, AppConstants.ADMIN_USER, false);
                displayName = user.getDisplayName();
                photoUrl = user.getPhotoUrl().toString();
            }
            // Write new user
            if (photoUrl != null && displayName != null)
                writeNewUser(user.getUid(), username, user.getEmail(), displayName, photoUrl, moderatorFlag);
            else if (user.getDisplayName() != null)
                writeNewUser(user.getUid(), username, user.getEmail(), displayName, null, moderatorFlag);
            else
                writeNewUser(user.getUid(), username, user.getEmail(), username, null, moderatorFlag);

            AppUtil.getDynamicLink(this);

            //Redirecting on basis of first time login or not
            boolean firstTimeLogin = false;//AppUtil.getBoolean(this, AppConstants.FIRST_TIME_LOGIN, true);
            if (firstTimeLogin)
                startActivity(new Intent(SignInActivity.this, AppIntroActivity.class));
            else
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

/*
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        return result;
    }
*/

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email, String displayName, String photoUrl, String moderatorFlag) {
        User user = new User(name, email, displayName, photoUrl, moderatorFlag);

        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_gmail_sign_in) {
            if (AppUtil.isInternetConnected(this)) {
                gmailSignIn();
            } else {
                showNoInternetConnectionDialog();
            }
        }
    }

    private void showNoInternetConnectionDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.no_internet_header))
                .setMessage(getResources().getString(R.string.no_internet_message))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }


    private void gmailSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(SignInActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            onAuthSuccess(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

}
