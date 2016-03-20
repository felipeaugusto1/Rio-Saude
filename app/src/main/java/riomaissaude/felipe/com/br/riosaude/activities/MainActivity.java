package riomaissaude.felipe.com.br.riosaude.activities;

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
import java.util.Calendar;
import java.util.List;

import riomaissaude.felipe.com.br.riosaude.R;
import riomaissaude.felipe.com.br.riosaude.db.DatabaseHandler;
import riomaissaude.felipe.com.br.riosaude.models.Estabelecimento;
import riomaissaude.felipe.com.br.riosaude.models.EstabelecimentoWs;
import riomaissaude.felipe.com.br.riosaude.models.StatusEstabelecimento;
import riomaissaude.felipe.com.br.riosaude.singleton.ListaEstabelecimentosSingleton;
import riomaissaude.felipe.com.br.riosaude.utils.LeitorArquivoEstabelecimentos;
import riomaissaude.felipe.com.br.riosaude.utils.PreferenciasUtil;
import riomaissaude.felipe.com.br.riosaude.utils.StringUtil;
import riomaissaude.felipe.com.br.riosaude.utils.ToastUtil;
import riomaissaude.felipe.com.br.riosaude.utils.ValidatorUtil;
import riomaissaude.felipe.com.br.riosaude.utils.WebService;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

/**
 *
 * Activity responsável por exibir o mapa com todos os estabelecimentos
 * de saúde do estado do RJ.
 *
 * Principais características:
 *
 * 1 - Ao abrir o aplicativo pela primeira vez, é realizado o cadastro de todos os
 * estabelecimentos no banco de dados.
 *
 * 2 - Popula o singleton ListaEstabelecimentosSingles, que é utilizado por todo o aplicativo.
 *
 * 3 - Ao clicar em um marcador, é aberto a activity DetalheEstabelecimento.
 *
 * 4 - Ao abrir o aplicativo, é realizada a sincronização com o WS, de todos os Estabelecimentos
 * que possuem media diferente de zero ou status diferente de Não Informado.
 *
 * 5 - Ao abrir o aplicativo pela primeira vez no dia, é alterado o status de todos
 * os estabelecimentos para Não Informado.
 *
 * Created by felipe on 9/13/15.
 */
public class MainActivity extends AppCompatActivity {

    private List<Estabelecimento> listaEstabelecimentos;
    private List<Estabelecimento> listaEstabelecimentosCopia;
    private SupportMapFragment mapFragment;
    private GoogleMap mapa;
    private Toolbar toolbar;
    private DatabaseHandler database;
    private ClusterManager<Estabelecimento> clusterManager;
    private Estabelecimento estabelecimentoClicado;
    private List<String> bairros;
    private List<EstabelecimentoWs> listaEstabelecimentoWs;

    private ProgressDialog dialog;

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
            buscarEstabelecimentosAlteradosWs();

        if (PreferenciasUtil.getPreferencias(PreferenciasUtil.KEY_PREFERENCIAS_DICAS_MAPA, MainActivity.this).equalsIgnoreCase(PreferenciasUtil.VALOR_INVALIDO))
            dicas();

        checagemStatusDiaria();
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

        if (!ValidatorUtil.isNuloOuVazio(this.listaEstabelecimentos) && this.listaEstabelecimentos.size() > 0) {
            for (Estabelecimento e : this.listaEstabelecimentos) {
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
        }
    }

    private void carregarEstabelecimentos() {
        ListaEstabelecimentosSingleton.getInstancia().setLista(this.database.getAllEstabelecimentos());
        this.listaEstabelecimentosCopia = ListaEstabelecimentosSingleton.getInstancia().getLista().size() == 0 ? this.database.getAllEstabelecimentos() : ListaEstabelecimentosSingleton.getInstancia().getLista();
        this.listaEstabelecimentos = ListaEstabelecimentosSingleton.getInstancia().getLista().size() == 0 ? this.database.getAllEstabelecimentos() : ListaEstabelecimentosSingleton.getInstancia().getLista();

        if (ValidatorUtil.isNuloOuVazio(this.listaEstabelecimentos) || this.listaEstabelecimentos.size() == 0) {
            dialog.setMessage("Carregando marcadores no mapa pela primeira vez, pode levar 1 minuto...");

            LeitorArquivoEstabelecimentos leitorArquivoEstabelecimentos = new LeitorArquivoEstabelecimentos(this);
            try {
                leitorArquivoEstabelecimentos.lerArquivo();
            } catch (IOException e) {
                ToastUtil.criarToastCurto(this, "Ocorreu um erro na sincronização... ");
            }

            this.database.addEstabelecimentos(leitorArquivoEstabelecimentos.getEstabelecimentos());

            ListaEstabelecimentosSingleton.getInstancia().setLista(leitorArquivoEstabelecimentos.getEstabelecimentos());
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
        getMenuInflater().inflate(R.menu.menu_ao_redor, menu);
        getMenuInflater().inflate(R.menu.menu_lista_estabelecimentos, menu);
        getMenuInflater().inflate(R.menu.menu_filtrar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_lista_estabelecimentos) {
            startActivity(new Intent(MainActivity.this, ListaEstabelecimentos.class));
        } else if (id == R.id.action_filtrar) {
            criarDialogSelecionarBairros();
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

    private void criarDialogSelecionarBairros() {
        AlertDialog dialog;

        final CharSequence[] items = this.bairros.toArray(new CharSequence[this.bairros.size()]);

        final ArrayList seletedItems = new ArrayList();
        final ArrayList<String> itensSelecionados = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione o bairro");
        builder.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {
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
                                    if (listaEstabelecimentos.size() != 4466)
                                        listaEstabelecimentos = listaEstabelecimentosCopia;
                                } else {
                                    listaEstabelecimentos = new ArrayList<>();
                                    for (String itemSelecionado : itensSelecionados) {
                                        for (Estabelecimento e : ListaEstabelecimentosSingleton.getInstancia().getLista()) {
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
            bairros.add(0, "Todos");

            return "finalizado!!!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String resultado) {
            try {
                setUpClusterer();
                dialog.dismiss();
            } catch (Exception e) {

            }

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
                database.updateAvaliacaoEstabelecimento(e.getId(), e.getMedia(), e.getStatusEstabelecimento());
            }

            return "Finalizado!";
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

    private void buscarEstabelecimentosAlteradosWs() {
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ListaEstabelecimentosSingleton.getInstancia().getLista().size() != 4466) {
            this.listaEstabelecimentos = this.database.getAllEstabelecimentos();
            ListaEstabelecimentosSingleton.getInstancia().setLista(this.listaEstabelecimentos);
        }

        try {
            setUpClusterer();
        } catch(Exception e) {

        }

    }

    private void checagemStatusDiaria() {
        Calendar calendar = Calendar.getInstance();
        int diaAtual = calendar.get(Calendar.DAY_OF_WEEK);

        String valor = PreferenciasUtil.getPreferencias(PreferenciasUtil.KEY_PREFERENCIAS_DIA_ATUAL_VERIFICACAO, MainActivity.this);
        if (valor.equalsIgnoreCase(PreferenciasUtil.VALOR_INVALIDO) || (StringUtil.isNumero(valor) && Integer.parseInt(valor) != diaAtual)) {
            this.database.resetarStatusEstabelecimentos();
            ListaEstabelecimentosSingleton.getInstancia().resetarStatusEstabelecimentos();
            PreferenciasUtil.salvarPreferencias(PreferenciasUtil.KEY_PREFERENCIAS_DIA_ATUAL_VERIFICACAO, String.valueOf(diaAtual), MainActivity.this);
        }
    }

}
