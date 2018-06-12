package testeadapt3.cursoandroid2.com.logindofacebook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import testeadapt3.cursoandroid2.com.logindofacebook.autenticacao.AutenticacaoGoogle;
import testeadapt3.cursoandroid2.com.logindofacebook.autenticacao.BaseActivity;
import testeadapt3.cursoandroid2.com.logindofacebook.autenticacao.CriarcontaActivity;
import testeadapt3.cursoandroid2.com.logindofacebook.autenticacao.Perfil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth autenticacaoFireBase;
    public EditText email;
    private EditText password;
    private TextView status;
    private TextView detalhe;
    private CallbackManager callbackManagerFacebook;
    public BaseActivity baseActivity;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.statusid);
        detalhe = findViewById(R.id.detalheId);
        email = findViewById(R.id.edt_textEmailId);
        password = findViewById(R.id.edt_textPassordId);
        findViewById(R.id.btnlogar_Id).setOnClickListener(this);
        findViewById(R.id.btnDeslogar_Id).setOnClickListener(this);
        findViewById(R.id.btnCriarConta_id).setOnClickListener(this);
        findViewById(R.id.google_signinButton_Id).setOnClickListener(this);

        autenticacaoFireBase = FirebaseAuth.getInstance();
        baseActivity = new BaseActivity();

        callbackManagerFacebook = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManagerFacebook, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("loginface", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("loginface", "facebook:onCancel");
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("loginface", "facebook:onError", error);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = autenticacaoFireBase.getCurrentUser();
        updateUI(currentUser);
    }


    private void createAccount() {
        Log.d("criarconta", "createconta");

        Intent intent = new Intent(MainActivity.this, CriarcontaActivity.class);
        startActivity(intent);
        finish();

    }

    private void signIn(String email, String password) {
        Log.d("signin", "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        baseActivity.showProgressDialog(this);
        // [START sign_in_with_email]
        autenticacaoFireBase.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signin", "Sucesso no login com email");
                            toastMessage("Sucesso na autenticacao com email");
                            FirebaseUser user = autenticacaoFireBase.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signin", "falha no login por email", task.getException());
                            toastMessage("Falha na autenticação com email");
                            updateUI(null);
                        }
                        // [START_EXCLUDE]
                        baseActivity.hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        callbackManagerFacebook.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("handle", "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        baseActivity.showProgressDialog(this);
        // [END_EXCLUDE]
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        autenticacaoFireBase.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("credencial", "signInWithCredential:success");
                            FirebaseUser user = autenticacaoFireBase.getCurrentUser();
                            updateUI(user);
                            singnInFacebook();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("falha", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Falha na autenticacao da credencial face.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        // [START_EXCLUDE]
                        baseActivity.hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void signOut() {
        autenticacaoFireBase.signOut();
        LoginManager.getInstance().logOut();
        updateUI(null);
    }

    private boolean validateForm() {
        boolean valid = true;
        String usuarioemail = this.email.getText().toString();

        if (TextUtils.isEmpty(usuarioemail)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String usuarioPassword = this.password.getText().toString();
        if (TextUtils.isEmpty(usuarioPassword)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }
        return valid;
    }

    public void updateUI(FirebaseUser user) {

        baseActivity.hideProgressDialog();
        if (user != null) {
            status.setText(getString(R.string.facebook_status_fmt, user.getDisplayName()));
            detalhe.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.btnlogar_Id).setVisibility(View.GONE);
            findViewById(R.id.btnDeslogar_Id).setVisibility(View.VISIBLE);
        } else {

            status.setText(R.string.signed_out);
            detalhe.setText(null);
            findViewById(R.id.button_facebook_login).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        int numero = view.getId();
        switch (numero) {
            case R.id.btnlogar_Id:
                signIn(email.getText().toString(), password.getText().toString());
                break;
            case R.id.btnDeslogar_Id:
                signOut();
                break;
            case R.id.btnCriarConta_id:
                createAccount();
                break;
            case R.id.google_signinButton_Id:
                siginGoogle();
                break;

        }
    }

    private void siginGoogle() {
        Intent intent = new Intent(MainActivity.this, AutenticacaoGoogle.class);
        startActivity(intent);
        finish();
    }

    private void toastMessage(String message) {

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void singnInFacebook() {
        Intent intent = new Intent(MainActivity.this, Perfil.class);
        startActivity(intent);
        finish();
    }
}
