package riomaissaude.felipe.com.br.riomaissaude.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.apache.http.Header;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import riomaissaude.felipe.com.br.riomaissaude.R;
import riomaissaude.felipe.com.br.riomaissaude.db.DatabaseHandler;
import riomaissaude.felipe.com.br.riomaissaude.models.Estabelecimento;
import riomaissaude.felipe.com.br.riomaissaude.models.EstabelecimentoWs;
import riomaissaude.felipe.com.br.riomaissaude.utils.StringUtil;
import riomaissaude.felipe.com.br.riomaissaude.utils.ValidatorUtil;
import riomaissaude.felipe.com.br.riomaissaude.utils.WebService;


/**
 * Created by felipe on 9/4/15.
 */
public class DetalheEstabelecimento extends AppCompatActivity {


    private SupportMapFragment mapFragment;
    private GoogleMap mapa;
    private Drawer navigationDrawer;
    private Estabelecimento estabelecimento;
    private DatabaseHandler database;
    private Toolbar toolbar;
    private TextView txtNomeEstabelecimento, txtEnderecoEstabelecimento, txtTipoEstabelecimento, txtTelefoneEstabelecimento;
    private RatingBar ratingBarAvaliacao;
    private ImageButton btnLigar;
    private RequestParams parametros;
    private EstabelecimentoWs estabelecimentoAtualizado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_estabelecimento);

        criarReferenciasComponentes();

        this.toolbar.setTitle("Detalhe");
        this.toolbar.setLogo(R.mipmap.ic_launcher);
        this.toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(this.toolbar);

        this.navigationDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(this.toolbar)
                .build();

        this.navigationDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle b = getIntent().getExtras();

        if (!ValidatorUtil.isNuloOuVazio(b)) {
            String estabelecimento_id = b.getString("estabelecimento_id");
            this.estabelecimento = this.database.getByPrimaryKey(Integer.parseInt(estabelecimento_id));

            Log.d("esta", this.estabelecimento.getId() + " - " +this.estabelecimento.getMedia());
        }

        percorrerOcorrencias();
    }

    private void criarReferenciasComponentes() {
        this.database = new DatabaseHandler(getApplicationContext());

        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        this.txtNomeEstabelecimento = (TextView) findViewById(R.id.txtNomeEstabelecimento);
        this.txtEnderecoEstabelecimento = (TextView) findViewById(R.id.txtEnderecoEstabelecimento);
        this.txtTipoEstabelecimento = (TextView) findViewById(R.id.txtTipoEstabelecimento);
        this.txtTelefoneEstabelecimento = (TextView) findViewById(R.id.txtTelefoneEstabelecimento);
        this.btnLigar = (ImageButton) findViewById(R.id.btnLigar);
        this.ratingBarAvaliacao = (RatingBar) findViewById(R.id.ratingBarAvaliacao);

        this.toolbar = (Toolbar) findViewById(R.id.main_toolbar);
    }

    private void percorrerOcorrencias() {
        try {
            configurarMapa();
            adicionarMarcador();
        } catch (Exception e) {

        }
    }

    private void configurarMapa() {
        if (this.mapa == null) {
            this.mapa = this.mapFragment.getMap();

            if (this.mapa != null) {
                this.mapa.setMyLocationEnabled(true);
            }
        }
    }

    private void adicionarMarcador() {
        if (!ValidatorUtil.isNuloOuVazio(estabelecimento)) {

            this.txtNomeEstabelecimento.setText(this.estabelecimento.getNomeFantasia());
            this.txtEnderecoEstabelecimento.setText(this.estabelecimento.getLogradouro() + ", " + this.estabelecimento.getNumero() + ". " + this.estabelecimento.getBairro() + "\n" + this.estabelecimento.getComplemento());
            this.txtTipoEstabelecimento.setText(this.estabelecimento.getTipoEstabelecimento());
            this.txtTelefoneEstabelecimento.setText(this.estabelecimento.getTelefone());
            this.ratingBarAvaliacao.setRating(Float.parseFloat(this.estabelecimento.getMedia()));

            LatLng c = new LatLng(Double.parseDouble(this.estabelecimento.getLatitude()), Double.parseDouble(this.estabelecimento.getLongitude()));

            MarkerOptions markerOption = null;

            markerOption = new MarkerOptions().position(c).icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            this.mapa.addMarker(markerOption);

            this.mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    Double.parseDouble(this.estabelecimento.getLatitude()), Double.parseDouble(this.estabelecimento.getLongitude())), 10));

            this.mapa.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_estrela, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_avaliar:
                inflarAvaliacaoEstabelecimento();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void inflarAvaliacaoEstabelecimento() {
        AlertDialog.Builder customDialog
                = new AlertDialog.Builder(DetalheEstabelecimento.this);
        customDialog.setTitle("Avaliar Estabelecimento");

        LayoutInflater layoutInflater
                = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.avaliar_estabelecimento, null);

        customDialog.setPositiveButton(
                getResources().getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

                        double valor = ratingBar.getRating();
                        parametros = new RequestParams();
                        parametros.put("id", estabelecimento.getId());
                        parametros.put("nota", String.valueOf(Math.round(valor)));

                        votarWs();
                    }
                });

        customDialog.setNegativeButton(getResources().getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        customDialog.setView(view);
        customDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void gerenciarBotoes(View v) {
        Intent intent = null;
        String latitude = this.estabelecimento.getLatitude();
        String longitude = this.estabelecimento.getLongitude();

        switch (v.getId()) {
            case R.id.btnLigar:
                intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:".concat(this.estabelecimento.getTelefone())));
                break;
            case R.id.btnComoChegar:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + latitude + ">,<" + longitude + ">?q=<" + latitude + ">,<" + longitude + ">(" + this.estabelecimento.getNomeFantasia() + ")"));
                break;
        }

        startActivity(intent);
    }

    private void votarWs() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WebService.ENDERECO_WS.concat("estabelecimento/votar"), this.parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = "";
                try {
                    str = new String(bytes, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Gson gson = new GsonBuilder().setDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss").create();

                estabelecimentoAtualizado = gson.fromJson(str, EstabelecimentoWs.class);

                boolean b = database.updateEstabelecimento(estabelecimentoAtualizado.getId(), estabelecimentoAtualizado.getMedia());

                if (b) {
                    Estabelecimento e = database.getByPrimaryKey(estabelecimento.getId());

                    ratingBarAvaliacao.setRating(Float.parseFloat(e.getMedia()));
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }

        });
    }

}
