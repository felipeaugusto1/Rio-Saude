package riomaissaude.felipe.com.br.riomaissaude.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
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
import riomaissaude.felipe.com.br.riomaissaude.provider.SearchableProvider;
import riomaissaude.felipe.com.br.riomaissaude.singleton.ListaEstabelecimentosSingleton;
import riomaissaude.felipe.com.br.riomaissaude.utils.StringUtil;
import riomaissaude.felipe.com.br.riomaissaude.utils.ToastUtil;
import riomaissaude.felipe.com.br.riomaissaude.utils.ValidatorUtil;

/**
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
            this.toolbar.setSubtitle(this.listaEstabelecimentosAux.size() + " estabelecimentos encontrados.");

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

        for (Estabelecimento estabelecimento : this.listaEstabelecimentos) {
            if (StringUtil.retirarAcentosDaPalavra(estabelecimento.getNomeFantasia()).toLowerCase().contains(StringUtil.retirarAcentosDaPalavra(q.toLowerCase())) || StringUtil.retirarAcentosDaPalavra(estabelecimento.getBairro()).contains(StringUtil.retirarAcentosDaPalavra(q.toLowerCase()))) {
                this.listaEstabelecimentosAux.add(estabelecimento);
            }
        }

        if (this.listaEstabelecimentosAux.isEmpty()) {
            ToastUtil.criarToastLongoCentralizado(ListaEstabelecimentos.this, "Nenhum registro encontrado.");
        } else {
            if (this.listaEstabelecimentosAux.size() == 1)
                ToastUtil.criarToastLongoCentralizado(ListaEstabelecimentos.this, this.listaEstabelecimentosAux.size() + " registro encontrado.");
            else
                ToastUtil.criarToastLongoCentralizado(ListaEstabelecimentos.this, this.listaEstabelecimentosAux.size() + " registros encontrados.");
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

        this.listaEstabelecimentos = ListaEstabelecimentosSingleton.getInstancia().getLista(); //this.database.getAllEstabelecimentos();
        this.listaEstabelecimentosAux = ListaEstabelecimentosSingleton.getInstancia().getLista(); //this.database.getAllEstabelecimentos();
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

}
