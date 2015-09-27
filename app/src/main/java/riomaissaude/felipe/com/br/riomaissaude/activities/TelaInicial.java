package riomaissaude.felipe.com.br.riomaissaude.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import riomaissaude.felipe.com.br.riomaissaude.R;

public class TelaInicial extends AppCompatActivity {

    private TextView txtFrase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        this.txtFrase = (TextView) findViewById(R.id.txtFrase);
        this.txtFrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                new MaterialDialog.Builder(TelaInicial.this)
//                        .title("Aguarde")
//                        .content("Iniciando aplicativo...")
//                        .progress(true, 0)
//                        .show();

                /* new MaterialDialog.Builder(TelaInicial.this)
                        .title("Aguarde")
                        .content("Montando mapa...")
                        .progress(true, 0)
                        .progressIndeterminateStyle(true)
                        .show(); */

                startActivity(new Intent(TelaInicial.this, MainActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tela_inicial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
