package riomaissaude.felipe.com.br.riosaude.activities;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import java.util.ArrayList;
import java.util.List;

import riomaissaude.felipe.com.br.riosaude.R;
import riomaissaude.felipe.com.br.riosaude.db.DatabaseHandler;
import riomaissaude.felipe.com.br.riosaude.extras.RecyclerViewAdapterEstabelecimentos;
import riomaissaude.felipe.com.br.riosaude.models.Estabelecimento;
import riomaissaude.felipe.com.br.riosaude.provider.SearchableProvider;
import riomaissaude.felipe.com.br.riosaude.singleton.ListaEstabelecimentosSingleton;
import riomaissaude.felipe.com.br.riosaude.utils.StringUtil;
import riomaissaude.felipe.com.br.riosaude.utils.ToastUtil;

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

        this.adapter = new RecyclerViewAdapterEstabelecimentos(this.listaEstabelecimentosAux);
        recyclerView.setAdapter(adapter);

        handleSearch(getIntent());

    }

    private void handleSearch(Intent intent) {
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {

            String q = intent.getStringExtra(SearchManager.QUERY);

            filtrarEstabelecimentos(q);
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

        this.listaEstabelecimentosAux.addAll(this.database.findByNome(StringUtil.retirarAcentosDaPalavra(q)));

        this.toolbar.setSubtitle(this.listaEstabelecimentosAux.size() + " estabelecimentos encontrados.");

        if (this.listaEstabelecimentosAux.isEmpty()) {
            ToastUtil.criarToastLongoCentralizado(getApplicationContext(), "Nenhum registro encontrado.");
        } else {
            if (this.listaEstabelecimentosAux.size() == 1)
                ToastUtil.criarToastLongoCentralizado(getApplicationContext(), this.listaEstabelecimentosAux.size() + " registro encontrado.");
            else
                ToastUtil.criarToastLongoCentralizado(getApplicationContext(), this.listaEstabelecimentosAux.size() + " registros encontrados.");
        }

        this.adapter.notifyDataSetChanged();
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

        this.listaEstabelecimentos = ListaEstabelecimentosSingleton.getInstancia().getLista().size() == 0 ? this.database.getAllEstabelecimentos() : ListaEstabelecimentosSingleton.getInstancia().getLista();
        this.listaEstabelecimentosAux = ListaEstabelecimentosSingleton.getInstancia().getLista().size() == 0 ? this.database.getAllEstabelecimentos() : ListaEstabelecimentosSingleton.getInstancia().getLista();
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
            ToastUtil.criarToastCurto(this, "Historico excluído com sucesso.");
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