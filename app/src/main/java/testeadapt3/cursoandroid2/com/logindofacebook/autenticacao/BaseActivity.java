package testeadapt3.cursoandroid2.com.logindofacebook.autenticacao;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by laianeoliveira on 20/02/18.
 */

public class BaseActivity {

    public ProgressDialog progressDialog;

    public void showProgressDialog(Context context){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Carregando...");
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();

    }
    public void hideProgressDialog(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }

    }

}
