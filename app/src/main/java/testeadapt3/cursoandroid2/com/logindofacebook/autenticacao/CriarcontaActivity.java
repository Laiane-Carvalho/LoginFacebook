package testeadapt3.cursoandroid2.com.logindofacebook.autenticacao;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import testeadapt3.cursoandroid2.com.logindofacebook.R;

public class CriarcontaActivity extends AppCompatActivity {
    private FirebaseAuth autenticacaoFireBase;
    private EditText criarNome;
    private EditText criarEmail;
    private EditText criarSenha;
    private Button btnCriar;
    private BaseActivity baseActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criarconta);

        criarNome = findViewById(R.id.edt_formularioNome);
        criarEmail = findViewById(R.id.edt_formularioEmailId);
        criarSenha = findViewById(R.id.edt_formularioSenhaId);

        baseActivity = new BaseActivity();
        autenticacaoFireBase = FirebaseAuth.getInstance();

        btnCriar = findViewById(R.id.btnformCriarConta_id);
        btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(criarNome.getText().toString(),criarEmail.getText().toString(),criarSenha.getText().toString());
            }
        });



    }

    private void createAccount(String nome, String email, String password) {
        Log.d("criarconta", "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        baseActivity.showProgressDialog(this);
        // [START create_user_with_email]
        autenticacaoFireBase.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("criarconta", "createUserWithEmail:success");
                            toastMessage("conta criada com sucesso");
                            FirebaseUser user = autenticacaoFireBase.getCurrentUser();
                            user.getDisplayName();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("criarconta", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CriarcontaActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // [START_EXCLUDE]
                        baseActivity.hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;
        String usuarioNome = this.criarNome.getText().toString();

        if (TextUtils.isEmpty(usuarioNome)) {
            criarNome.setError("Required.");
            valid = false;
        } else {
            criarNome.setError(null);

        }
        String usuarioEmail = this.criarEmail.getText().toString();

        if (TextUtils.isEmpty(usuarioEmail)) {
            criarEmail.setError("Required.");
            valid = false;
        } else {
            criarEmail.setError(null);
        }

        String usuariosenha = this.criarSenha.getText().toString();
        if (TextUtils.isEmpty(usuariosenha)) {
            criarSenha.setError("Required.");
            valid = false;
        } else {
            criarSenha.setError(null);
        }
        return valid;
    }
    private void toastMessage(String message) {

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
