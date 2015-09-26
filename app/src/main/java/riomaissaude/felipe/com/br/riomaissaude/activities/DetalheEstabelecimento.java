package riomaissaude.felipe.com.br.riomaissaude.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;

import riomaissaude.felipe.com.br.riomaissaude.R;
import riomaissaude.felipe.com.br.riomaissaude.db.DatabaseHandler;
import riomaissaude.felipe.com.br.riomaissaude.models.Estabelecimento;
import riomaissaude.felipe.com.br.riomaissaude.utils.ValidatorUtil;


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
    private ImageButton btnLigar;

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

            Log.d("Estabelecimento", this.estabelecimento.toString());

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
        //getMenuInflater().inflate(R.menu.menu_compartilhar, menu);
        return true;
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

    public void gerenciarBotoes(View v)  {
        Intent intent = null;
        String latitude = this.estabelecimento.getLatitude();
        String longitude = this.estabelecimento.getLongitude();

        switch (v.getId()) {
            case R.id.btnLigar:
                intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:".concat(this.estabelecimento.getTelefone())));
                break;
            case R.id.btnComoChegar:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<"+latitude+">,<"+longitude+">?q=<"+latitude+">,<"+longitude+">("+this.estabelecimento.getNomeFantasia()+")"));
                break;
        }

        startActivity(intent);
    }
}
