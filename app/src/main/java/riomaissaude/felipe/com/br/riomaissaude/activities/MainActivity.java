package riomaissaude.felipe.com.br.riomaissaude.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.clustering.ClusterManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import riomaissaude.felipe.com.br.riomaissaude.R;
import riomaissaude.felipe.com.br.riomaissaude.db.DatabaseHandler;
import riomaissaude.felipe.com.br.riomaissaude.models.Estabelecimento;
import riomaissaude.felipe.com.br.riomaissaude.models.EstabelecimentoWs;
import riomaissaude.felipe.com.br.riomaissaude.models.StatusEstabelecimento;
import riomaissaude.felipe.com.br.riomaissaude.singleton.ListaEstabelecimentosSingleton;
import riomaissaude.felipe.com.br.riomaissaude.utils.PreferenciasUtil;
import riomaissaude.felipe.com.br.riomaissaude.utils.StringUtil;
import riomaissaude.felipe.com.br.riomaissaude.utils.ValidatorUtil;
import riomaissaude.felipe.com.br.riomaissaude.utils.WebService;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class MainActivity extends AppCompatActivity {

    private List<Estabelecimento> listaEstabelecimentos;
    private List<Estabelecimento> listaEstabelecimentosCopia;
    private SupportMapFragment mapFragment;
    private GoogleMap mapa;
    //private HashMap<Marker, Estabelecimento> marcadoresHashMap;
    //private static ProgressDialog progressDialog;
    private Toolbar toolbar;
    private DatabaseHandler database;
    private ClusterManager<Estabelecimento> clusterManager;
    private Estabelecimento estabelecimentoClicado;
    private List<String> bairros;
    private List<EstabelecimentoWs> listaEstabelecimentoWs;

    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.database = new DatabaseHandler(getApplicationContext());

        this.toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        this.toolbar.setTitle(getResources().getString(R.string.app_name));
        this.toolbar.setLogo(R.mipmap.ic_launcher2);
        this.toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(this.toolbar);

        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        this.configurarMapa();

        //this.database.deletarEstabelecimentos();

        new CarregarDadosMapa().execute("Carregando...");

        if (isConectado())
            buscarEstabelecimentosWs();

        if (PreferenciasUtil.getPreferencias(PreferenciasUtil.KEY_PREFERENCIAS_DICAS_MAPA, MainActivity.this).equalsIgnoreCase(PreferenciasUtil.VALOR_INVALIDO))
            dicas();

    }

    private void dicas() {
        PreferenciasUtil.salvarPreferencias(PreferenciasUtil.KEY_PREFERENCIAS_DICAS_MAPA, String.valueOf(Boolean.TRUE), MainActivity.this);

        new MaterialShowcaseView.Builder(this)
                .setTarget(this.toolbar)
                .setDismissText("Ok, entendi")
                .setDelay(200)
                .setContentText("Cheque nossas outras funcionalidades: \n \n - Visualizar estabelecimentos ao redor \n - Pesquisar estabelecimentos \n - Filtrar por bairros").show();

    }

    private void adicionarMarcadores() {
        this.mapa.clear();

        //this.marcadoresHashMap = new HashMap<Marker, Estabelecimento>();
        if (!ValidatorUtil.isNuloOuVazio(this.listaEstabelecimentos) && this.listaEstabelecimentos.size() > 0) {
            for (Estabelecimento e : this.listaEstabelecimentos) {
                try {
                    LatLng c = new LatLng(Double.parseDouble(e.getLatitude()), Double.parseDouble(e.getLongitude()));
                } catch (Exception ex) {
                }


                /* MarkerOptions markerOption = null;

                markerOption = new MarkerOptions().position(c).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                Marker marcadorAtual = this.mapa.addMarker(markerOption);

                if (!ValidatorUtil.isNuloOuVazio(marcadorAtual) && !ValidatorUtil.isNuloOuVazio(e)) {
                    this.marcadoresHashMap.put(marcadorAtual, e);

                }

                marcadorAtual.remove(); */

                //clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MyCustomAdapterForItems());

                //double offset = e.getId() / 60d;
                //lat = lat + offset;
                //lng = lng + offset;
                //MyItem offsetItem = new MyItem(e.get, lng);
                clusterManager.addItem(e);
            }
        }
        this.mapa.setInfoWindowAdapter(this.clusterManager.getMarkerManager());

        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MarkerInfoWindowAdapter());

        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Estabelecimento>() {
            @Override
            public boolean onClusterItemClick(Estabelecimento estabelecimento) {
                estabelecimentoClicado = estabelecimento;
                return false;
            }
        });

        this.mapa.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent telaDetalheOcorrencia = new Intent(MainActivity.this,
                        DetalheEstabelecimento.class);

                telaDetalheOcorrencia.putExtra("estabelecimento_id",
                        String.valueOf(estabelecimentoClicado.getId()));
                telaDetalheOcorrencia.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(telaDetalheOcorrencia);
            }
        });

    }

    private void configurarMapa() {
        if (ValidatorUtil.isNuloOuVazio(this.mapa)) {
            this.mapa = this.mapFragment.getMap();

            /* if (!ValidatorUtil.isNuloOuVazio(this.mapa)) {
                this.mapa.setMyLocationEnabled(true);
            } */
        }
    }

    private void carregarEstabelecimentos() {
        //this.listaEstabelecimentos = this.database.getAllEstabelecimentos();
        //this.listaEstabelecimentosCopia = this.listaEstabelecimentos;

        ListaEstabelecimentosSingleton.getInstancia().setLista(this.database.getAllEstabelecimentos());
        this.listaEstabelecimentosCopia = ListaEstabelecimentosSingleton.getInstancia().getLista();
        this.listaEstabelecimentos = ListaEstabelecimentosSingleton.getInstancia().getLista();

        if (ValidatorUtil.isNuloOuVazio(this.listaEstabelecimentos) || this.listaEstabelecimentos.size() == 0) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("estabelecimentos.csv")));
                String line;
                int i = 0;

                Estabelecimento e;
                while ((line = reader.readLine()) != null) {
                    e = new Estabelecimento();
                    String[] rowData = line.split(",");

                    e.setId(i++);
                    e.setCnes(String.valueOf(rowData[0]));
                    e.setCnpj(String.valueOf(rowData[1]));
                    e.setRazaoSocial(String.valueOf(rowData[2]));
                    e.setNomeFantasia(String.valueOf(rowData[3]));
                    e.setLogradouro(String.valueOf(rowData[4]));
                    e.setNumero(String.valueOf(rowData[5]));
                    e.setComplemento(String.valueOf(rowData[6]));
                    e.setBairro(String.valueOf(rowData[7]));
                    e.setCep(String.valueOf(rowData[8]));
                    e.setTelefone(String.valueOf(rowData[9]));
                    e.setFax(String.valueOf(rowData[10]));
                    e.setEmail(String.valueOf(rowData[11]));
                    e.setLatitude(String.valueOf(rowData[12]).replace("\"", ""));
                    e.setLongitude(String.valueOf(rowData[13]).replace("\"", ""));
                    e.setDataAtualizacaoCoordenadas(String.valueOf(rowData[14]));
                    e.setCodigoEsferaAdministrativa(String.valueOf(rowData[15]));
                    e.setEsferaAdministrativa(String.valueOf(rowData[16]));
                    e.setCodigoDaAtividade(String.valueOf(rowData[17]));
                    e.setAtividadeDestino(String.valueOf(rowData[18]));
                    e.setCodigoNaturezaOrganizacao(String.valueOf(rowData[19]));
                    e.setNaturezaOrganizacao(String.valueOf(rowData[20]));
                    e.setTipoUnidade(String.valueOf(rowData[21]));
                    e.setTipoEstabelecimento(String.valueOf(rowData[22]));
                    e.setMedia("0");
                    e.setStatusEstabelecimento(StatusEstabelecimento.SEM_INFORMACAO);

                    this.listaEstabelecimentos.add(e);
                }

            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Ocorreu um erro na sincronização... ", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.database.addEstabelecimentos(this.listaEstabelecimentos);
            ListaEstabelecimentosSingleton.getInstancia().setLista(this.listaEstabelecimentos);
        }

    }

    private void setUpClusterer() {
        // lat e long do RJ
        this.mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-22.9112335, -43.448334), 10));

        this.clusterManager = new ClusterManager<Estabelecimento>(this, this.mapa);

        this.mapa.setOnCameraChangeListener(clusterManager);
        this.mapa.setOnMarkerClickListener(clusterManager);

        adicionarMarcadores();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ao_redor, menu);
        getMenuInflater().inflate(R.menu.menu_lista_estabelecimentos, menu);
        getMenuInflater().inflate(R.menu.menu_filtrar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_lista_estabelecimentos) {

            /* dialog = new MaterialDialog.Builder(MainActivity.this)
                    .title(getResources().getString(R.string.aguarde))
                    .content("Listando estabelecimentos...")
                    .progress(true, 0)
                    .progressIndeterminateStyle(true).show(); */

            startActivity(new Intent(MainActivity.this, ListaEstabelecimentos.class));
        } else if (id == R.id.action_filtrar) {
            criarDialogSelecionarTiposOcorrencia();
        } else if (id == R.id.action_ao_redor) {
            startActivity(new Intent(MainActivity.this, AoRedor.class));
        }


        return super.onOptionsItemSelected(item);
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

    private void criarDialogSelecionarTiposOcorrencia() {
        AlertDialog dialog;

        final CharSequence[] items = this.bairros.toArray(new CharSequence[this.bairros.size()]);

        // arraylist to keep the selected items
        final ArrayList seletedItems = new ArrayList();
        final ArrayList<String> itensSelecionados = new ArrayList<String>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione o bairro");
        builder.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which
                    // checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                .setPositiveButton(
                        getResources().getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                for (int i = 0; i < seletedItems.size(); i++) {
                                    itensSelecionados.add(String
                                            .valueOf(items[(int) seletedItems.get(i)]));
                                }

                                if (itensSelecionados.contains("Todos")) {
                                    listaEstabelecimentos = listaEstabelecimentosCopia;
                                } else {
                                    listaEstabelecimentos = new ArrayList<Estabelecimento>();
                                    for (String itemSelecionado : itensSelecionados) {
                                        for (Estabelecimento e : listaEstabelecimentosCopia) {
                                            if (StringUtil.retirarAcentosDaPalavra(itemSelecionado.trim()).equalsIgnoreCase(StringUtil.retirarAcentosDaPalavra(e.getBairro())))
                                                listaEstabelecimentos.add(e);
                                        }
                                    }
                                }


                                if (listaEstabelecimentos.size() == 1)
                                    Toast.makeText(MainActivity.this, listaEstabelecimentos.size() + " estabelecimento encontrado.", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(MainActivity.this, listaEstabelecimentos.size() + " estabelecimentos encontrados.", Toast.LENGTH_LONG).show();

                                setUpClusterer();
                            }
                        })
                .setNegativeButton(getResources().getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

        dialog = builder.create();
        dialog.show();
    }

    private class CarregarDadosMapa extends AsyncTask<String, Integer, String> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle(getResources().getString(R.string.aguarde));
            dialog.setMessage("Carregando marcadores no mapa...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            carregarEstabelecimentos();

            bairros = database.getAllBairros();

            return "finalizado!!!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String resultado) {
            setUpClusterer();
            dialog.dismiss();
        }
    }

    private class SincronizarEstabelecimentos extends AsyncTask<String, Integer, String> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle("Aguarde");
            dialog.setMessage("Sincronizando estabelecimentos...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            for (EstabelecimentoWs e: listaEstabelecimentoWs) {
                Log.d("Indo atualizar", e.getId() + " - " + e.getMedia());
                /* for (Estabelecimento es: ListaEstabelecimentosSingleton.getInstancia().getLista()) {
                    if (e.getId() == es.getId()) {
                        es.setMedia(String.valueOf(e.getMedia()));
                        es.setStatusEstabelecimento(e.getStatusEstabelecimento());
                    }

                } */
                database.updateAvaliacaoEstabelecimento(e.getId(), e.getMedia(), e.getStatusEstabelecimento());
            }

            //database.updateEstabelecimentos(listaEstabelecimentoWs);
            return "finalizado!!!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String resultado) {
            dialog.dismiss();
            new CarregarDadosMapa().execute("Carregando...");
        }
    }

    private void buscarEstabelecimentosWs() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WebService.ENDERECO_WS.concat("estabelecimento/listar"), new AsyncHttpResponseHandler() {

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

                Type listType = new TypeToken<ArrayList<EstabelecimentoWs>>() {
                }.getType();
                listaEstabelecimentoWs = gson.fromJson(str, listType);

                new SincronizarEstabelecimentos().execute("Sincronizar...");
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }

        });
    }

    private boolean isConectado() {
        ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connec != null && (
                (connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) ||
                        (connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED))) {
            return true;
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpClusterer();
    }

}
