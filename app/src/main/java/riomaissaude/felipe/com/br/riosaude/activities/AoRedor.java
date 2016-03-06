package riomaissaude.felipe.com.br.riosaude.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import riomaissaude.felipe.com.br.riosaude.R;
import riomaissaude.felipe.com.br.riosaude.db.DatabaseHandler;
import riomaissaude.felipe.com.br.riosaude.gps.FusedLocationPosition;
import riomaissaude.felipe.com.br.riosaude.models.Estabelecimento;
import riomaissaude.felipe.com.br.riosaude.utils.ToastUtil;
import riomaissaude.felipe.com.br.riosaude.utils.ValidatorUtil;

/**
 * Activity responsável por exibir a localização do usuário, e os estabelecimentos
 * ao seu redor, dado um raio.
 *
 * Principais características:
 *
 * 1 - Para recuperar a posição do usuário, é necessário que o GPS esteja ativado.
 *
 * 2 - Para recuperar a posição do usuário, é utilziado o FusedLocationProvider,
 * para garantir uma localização mais precisa.
 *
 * 3 - O raio pode ser alterado de 0 a 50km.
 *
 * Created by felipe on 9/20/15.
 */
public class AoRedor extends AppCompatActivity {

    private Drawer navigationDrawer;
    private Toolbar toolbar;

    private SupportMapFragment mapFragment;
    private LocationRequest locationRequest;
    private GoogleMap mapa;

    private static ProgressDialog progressDialog;
    private List<Estabelecimento> listaOcorrencias;
    private HashMap<Marker, Estabelecimento> marcadoresHashMap;
    private static final long ONE_MIN = 1000 * 60;

    private int distancia_raio = 1000;
    private Location localizacaoUsuario;
    private FusedLocationPosition fusedLocationService;
    private LocationManager myLocationManager;

    private Estabelecimento estabelecimentoClicado;
    private DatabaseHandler database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ao_redor);

        this.database = new DatabaseHandler(getApplicationContext());

        this.fusedLocationService = new FusedLocationPosition(this);

        this.myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!this.myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertaGPSDesativado();
            ToastUtil.criarToastLongo(AoRedor.this, getResources().getString(R.string.msgAtivarGps));
        } else
            recuperarPosicaoUsuario();


        this.toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        this.toolbar.setTitle(getResources().getString(R.string.tela_ao_redor));
        this.toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(this.toolbar);

        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

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
                Intent intent = new Intent(AoRedor.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        this.configurarMapa();

        this.atualizarMapa();

        this.mapa.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                final Estabelecimento estabelecimento = marcadoresHashMap.get(marker);

                Intent telaDetalheEstabelecimento = new Intent(AoRedor.this,
                        DetalheEstabelecimento.class);

                telaDetalheEstabelecimento.putExtra("estabelecimento_id",
                        String.valueOf(estabelecimento.getId()));
                telaDetalheEstabelecimento.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(telaDetalheEstabelecimento);
            }
        });
    }

    private void recuperarPosicaoUsuario() {
        try {
            progressDialog = ProgressDialog.show(AoRedor.this,
                    getResources().getString(R.string.aguarde),
                    getResources().getString(R.string.recuperandoPosicao));
        } catch (Exception e) {
        }


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                localizacaoUsuario = fusedLocationService.getLocation();
                adicionarMarcadorPosicaoUsuario();
                criarCirculo();
                adicionarMarcadores();
                progressDialog.dismiss();
            }
        }, 5000);
    }

    private void configurarMapa() {
        if (ValidatorUtil.isNuloOuVazio(this.mapa)) {
            this.mapa = this.mapFragment.getMap();

            /* if (!ValidatorUtil.isNuloOuVazio(this.mapa)) {
                this.mapa.setMyLocationEnabled(true);
            } */
        }
    }

    private void atualizarMapa() {
        this.listaOcorrencias = new ArrayList<Estabelecimento>();
        this.marcadoresHashMap = new HashMap<Marker, Estabelecimento>();

        this.listaOcorrencias = database.getAllEstabelecimentos();

        percorrerEstabelecimentos();
    }

    private void adicionarMarcadorPosicaoUsuario() {
        this.mapa.clear();

        if (ValidatorUtil.isNuloOuVazio(this.localizacaoUsuario))
            recuperarPosicaoUsuario();

        if (!ValidatorUtil.isNuloOuVazio(this.localizacaoUsuario)) {
            MarkerOptions markerOption = null;
            markerOption = new MarkerOptions().position(
                    new LatLng(this.localizacaoUsuario.getLatitude(), this.localizacaoUsuario
                            .getLongitude())).icon(
                    BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED));

            this.mapa.addMarker(markerOption);
        }


        //this.mapa.animateCamera(CameraUpdateFactory.zoomTo(14));
        //this.mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.localizacaoUsuario.getLatitude(), this.localizacaoUsuario.getLongitude()), 10));
    }

    private void criarCirculo() {
        Circle circle = null;
        try {
            this.mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(this.localizacaoUsuario.getLatitude(),
                            this.localizacaoUsuario.getLongitude()), 10));

            this.mapa.animateCamera(CameraUpdateFactory.zoomTo(14));

            circle = this.mapa.addCircle(new CircleOptions()
                    .center(new LatLng(this.localizacaoUsuario.getLatitude(),
                            this.localizacaoUsuario.getLongitude()))
                    .radius(distancia_raio).strokeWidth(1)
                    .strokeColor(Color.BLACK));

            // verde: 0x7F00FF00
            // amarelo: 0x40e5ff00
            // vermelho: 0x40ff0000
            circle.setFillColor(0x40ff0000);
        } catch (Exception e) {
        }


    }

    private void percorrerEstabelecimentos() {
        this.mapa.clear();

        try {
            configurarMapa();
        } catch (Exception e) {
            if (!ValidatorUtil.isNuloOuVazio(progressDialog))
                progressDialog.dismiss();
        }
    }

    private void adicionarMarcadores() {
        int contadorEstabelecimentos = 0;

        try {
            if (!ValidatorUtil.isNuloOuVazio(listaOcorrencias) && listaOcorrencias.size() > 0) {
                for (Estabelecimento estabelecimento : this.listaOcorrencias) {
                    double result = SphericalUtil.computeDistanceBetween(
                            new LatLng(this.localizacaoUsuario.getLatitude(), this.localizacaoUsuario.getLongitude()), new LatLng(Double.parseDouble(estabelecimento
                                    .getLatitude()), Double.parseDouble(estabelecimento.getLongitude())));

                    if (result < distancia_raio) {
                        contadorEstabelecimentos++;
                        LatLng c = new LatLng(Double.parseDouble(estabelecimento.getLatitude()),
                                Double.parseDouble(estabelecimento.getLongitude()));

                        MarkerOptions markerOption = null;

                        markerOption = new MarkerOptions().position(c).icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                        Marker marcadorAtual = this.mapa.addMarker(markerOption);

                        if (!ValidatorUtil.isNuloOuVazio(marcadorAtual)) {
                            this.marcadoresHashMap.put(marcadorAtual, estabelecimento);
                            this.mapa.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
                        }
                    }
                }

                ToastUtil.criarToastLongo(AoRedor.this, contadorEstabelecimentos + " estabelecimento(s) encontrada(s) em um raio de " + (distancia_raio / 100) + "km.");
            }

            progressDialog.dismiss();
        } catch (Exception ex) {
            progressDialog.dismiss();
            ToastUtil.criarToastLongo(AoRedor.this, "Ocorreu um erro ao tentar recuperar sua posição. Por favor, tente novamente mais tarde.");
            startActivity(new Intent(AoRedor.this, MainActivity.class));
        }

        this.mapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                estabelecimentoClicado = marcadoresHashMap.get(marker);
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        this.navigationDrawer.setSelection(0);
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

    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        public MarkerInfoWindowAdapter() {
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View v = null;
            try {
                if (!ValidatorUtil.isNuloOuVazio(estabelecimentoClicado)) {
                    v = inflater.inflate(R.layout.descricao_estabelecimento_mapa, null);

                    TextView txtNome = (TextView) v.findViewById(R.id.txtNomeFantasia);
                    TextView txtLogradouro = (TextView) v.findViewById(R.id.txtLogradouro);
                    TextView txtTelefone = (TextView) v.findViewById(R.id.txtTelefone);
                    TextView txtStatusAtual = (TextView) v.findViewById(R.id.txtStatusAtual);

                    txtNome.setText(estabelecimentoClicado.getNomeFantasia());
                    txtLogradouro.setText(estabelecimentoClicado.getLogradouro());
                    txtTelefone.setText(estabelecimentoClicado.getTelefone());
                    txtStatusAtual.setText("Situação atual: " +estabelecimentoClicado.getStatusEstabelecimento());
                }
            } catch (Exception e) {
            }

            return v;
        }
    }

    private void alertaGPSDesativado() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage(getResources().getString(R.string.gpsDesativado))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ativar),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton(
                getResources().getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_aumentar_raio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_aumentar_raio:
                inflarAumentarRaio();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void inflarAumentarRaio() {
        AlertDialog.Builder customDialog
                = new AlertDialog.Builder(AoRedor.this);
        customDialog.setTitle("Alterar raio de busca");

        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.aumentar_raio_estabelecimentos, null);

        final TextView txtRaio = (TextView) view.findViewById(R.id.txtRaioEscolhido);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);

        txtRaio.setText("0km");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distancia_raio = progress;
                txtRaio.setText(distancia_raio+"km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ToastUtil.criarToastCurto(AoRedor.this, distancia_raio+"km");
            }
        });

        customDialog.setPositiveButton(
                getResources().getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        distancia_raio = distancia_raio * 100;
                        recuperarPosicaoUsuario();
                    }
                });

        customDialog.setNegativeButton(getResources().getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        customDialog.setView(view);
        customDialog.show();
    }

}
