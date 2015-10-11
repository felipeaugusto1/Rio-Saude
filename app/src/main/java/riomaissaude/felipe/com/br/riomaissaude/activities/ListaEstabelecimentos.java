package riomaissaude.felipe.com.br.riomaissaude.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import riomaissaude.felipe.com.br.riomaissaude.R;
import riomaissaude.felipe.com.br.riomaissaude.db.DatabaseHandler;
import riomaissaude.felipe.com.br.riomaissaude.extras.RecyclerViewAdapterEstabelecimentos;
import riomaissaude.felipe.com.br.riomaissaude.models.Estabelecimento;
import riomaissaude.felipe.com.br.riomaissaude.models.EstabelecimentoWs;
import riomaissaude.felipe.com.br.riomaissaude.provider.SearchableProvider;
import riomaissaude.felipe.com.br.riomaissaude.singleton.ListaEstabelecimentosSingleton;
import riomaissaude.felipe.com.br.riomaissaude.utils.StringUtil;
import riomaissaude.felipe.com.br.riomaissaude.utils.ToastUtil;
import riomaissaude.felipe.com.br.riomaissaude.utils.ValidatorUtil;

/**
 * Activity responsável por listar todos os estabelecimentos.
 *
 * Principais características:
 *
 * 1 - É possível pesquisar por estabelecimentos, informando uma palvra chave, ou falando
 * uma palavra chave (tipo de especialidade, bairro, nome).
 *
 * 2 - Esta activity é formada por CardViews.
 *
 * 3 - Ao clicar em um cardview, é aberta a activity DetalheEstabelecimento.
 *
 * Created by felipe on 9/13/15.
 */
public class ListaEstabelecimentos extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<Estabelecimento> listaEstabelecimentos;
    private List<Estabelecimento> listaEstabelecimentosAux;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Toolbar toolbar;

    private RecyclerViewAdapterEstabelecimentos adapter;

    private CoordinatorLayout layoutListaEstabelecimentos;

    private DatabaseHandler database;

    private Drawer navigationDrawer;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_estabelecimentos);

        this.database = new DatabaseHandler(getApplicationContext());

        this.layoutListaEstabelecimentos = (CoordinatorLayout) findViewById(R.id.layoutListaEstabelecimentos);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar_lista_estabelecimentos);
        this.toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        this.toolbar.setTitle("Pesquisar");
        this.toolbar.setLogo(R.mipmap.ic_launcher2);
        setSupportActionBar(this.toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.linearLayoutManager = new LinearLayoutManager(this);
        this.recyclerView = (RecyclerView) findViewById(R.id.cardList);
        this.recyclerView.setHasFixedSize(true);
        this.linearLayoutManager = new LinearLayoutManager(this);
        this.linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.recyclerView.setLayoutManager(this.linearLayoutManager);
        //this.mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        //mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark, android.R.color.holo_green_dark, android.R.color.holo_blue_bright);

        /* mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        }); */

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

        carregarEstabelecimentos();

        this.adapter = new RecyclerViewAdapterEstabelecimentos(ListaEstabelecimentosSingleton.getInstancia().getLista());
        recyclerView.setAdapter(adapter);

        handleSearch(getIntent());

    }

    private void handleSearch(Intent intent) {
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {

            String q = intent.getStringExtra(SearchManager.QUERY);

            //filtrarEstabelecimentos(q);
            new PesquisarEstabelecimentos().execute(q);
            this.toolbar.setTitle(q);

            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this, SearchableProvider.AUTHORITY, SearchableProvider.MODE);
            searchRecentSuggestions.saveRecentQuery(q, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleSearch(intent);
    }

    private void filtrarEstabelecimentos(String q) {
        this.listaEstabelecimentosAux.clear();

        for (Estabelecimento estabelecimento : ListaEstabelecimentosSingleton.getInstancia().getLista()) {
            if (StringUtil.retirarAcentosDaPalavra(estabelecimento.getNomeFantasia()).toLowerCase().contains(StringUtil.retirarAcentosDaPalavra(q.toLowerCase()))
                    || StringUtil.retirarAcentosDaPalavra(estabelecimento.getBairro().toLowerCase()).contains(StringUtil.retirarAcentosDaPalavra(q.toLowerCase()))
                    || StringUtil.retirarAcentosDaPalavra(estabelecimento.getComplemento().toLowerCase()).contains(StringUtil.retirarAcentosDaPalavra(q.toLowerCase()))
                    || StringUtil.retirarAcentosDaPalavra(estabelecimento.getLogradouro().toLowerCase()).contains(StringUtil.retirarAcentosDaPalavra(q.toLowerCase()))
                    || StringUtil.retirarAcentosDaPalavra(estabelecimento.getRazaoSocial().toLowerCase()).contains(StringUtil.retirarAcentosDaPalavra(q.toLowerCase()))
                    || StringUtil.retirarAcentosDaPalavra(estabelecimento.getTipoEstabelecimento().toLowerCase()).contains(StringUtil.retirarAcentosDaPalavra(q.toLowerCase()))
                    || StringUtil.retirarAcentosDaPalavra(estabelecimento.getAtividadeDestino().toLowerCase()).contains(StringUtil.retirarAcentosDaPalavra(q.toLowerCase()))) {
                Log.d("adding", estabelecimento.getRazaoSocial());
                this.listaEstabelecimentosAux.add(estabelecimento);
            }
        }
    }

    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //mAdapter.refreshContent();
                //mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 5000);
    }

    private void carregarEstabelecimentos() {
        this.listaEstabelecimentos = new ArrayList<Estabelecimento>();
        this.listaEstabelecimentosAux = new ArrayList<Estabelecimento>();

        //this.listaEstabelecimentos = ListaEstabelecimentosSingleton.getInstancia().getLista().size() == 0 ? this.database.getAllEstabelecimentos() : ListaEstabelecimentosSingleton.getInstancia().getLista();
        //this.listaEstabelecimentosAux = ListaEstabelecimentosSingleton.getInstancia().getLista().size() == 0 ? this.database.getAllEstabelecimentos() : ListaEstabelecimentosSingleton.getInstancia().getLista();

        //Log.d("ttttamanho da singleton", this.listaEstabelecimentos+"");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView;
        MenuItem item = menu.findItem(R.id.menu_search);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            searchView = (SearchView) item.getActionView();
        } else {
            searchView = (SearchView) MenuItemCompat.getActionView(item);
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Palavra-chave");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_delete) {
            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this, SearchableProvider.AUTHORITY, SearchableProvider.MODE);
            searchRecentSuggestions.clearHistory();
            Toast.makeText(this, "Historico excluído com sucesso.", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private class PesquisarEstabelecimentos extends AsyncTask<String, Integer, String> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(ListaEstabelecimentos.this);
            dialog.setTitle("Aguarde");
            dialog.setMessage("Pesquisando estabelecimentos...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            filtrarEstabelecimentos(params[0]);
            return "Finalizado!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String resultado) {
            dialog.dismiss();

            toolbar.setSubtitle(listaEstabelecimentosAux.size() + " estabelecimentos encontrados.");

            if (listaEstabelecimentosAux.isEmpty()) {
                ToastUtil.criarToastLongoCentralizado(getApplicationContext(), "Nenhum registro encontrado.");
            } else {
                if (listaEstabelecimentosAux.size() == 1)
                    ToastUtil.criarToastLongoCentralizado(getApplicationContext(), listaEstabelecimentosAux.size() + " registro encontrado.");
                else
                    ToastUtil.criarToastLongoCentralizado(getApplicationContext(), listaEstabelecimentosAux.size() + " registros encontrados.");
            }

            adapter.notifyDataSetChanged();

        }
    }
}
