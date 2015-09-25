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
        this.toolbar.setTitle("Buscar Estabelecimento");
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

        Log.d("pesquisando por", q);
        Log.d("tamanho da lista", this.listaEstabelecimentos.size()+"");

        for (Estabelecimento estabelecimento : this.listaEstabelecimentos) {
            if (estabelecimento.getNomeFantasia().toLowerCase().startsWith(q.toLowerCase()) || estabelecimento.getAtividadeDestino().toLowerCase().startsWith(q.toLowerCase())
                    || estabelecimento.getBairro().toLowerCase().startsWith(q.toLowerCase())) {
                this.listaEstabelecimentosAux.add(estabelecimento);
                Log.d("entrou", "entrou");
            }

        }

        if (this.listaEstabelecimentosAux.isEmpty()) {
            //this.recyclerView.setVisibility(View.GONE);

            //TextView tv = new TextView(this);
            //tv.setText("Nenhum registro encontrado");
            //tv.setId(1);

            //tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            //tv.setGravity(Gravity.CENTER);
            //this.layoutListaEstabelecimentos.addView(tv);
            Toast.makeText(ListaEstabelecimentos.this, "Nenhum registro encontrado.", Toast.LENGTH_LONG).show();
        } else {
            if (this.listaEstabelecimentos.size() == 1)
                Toast.makeText(ListaEstabelecimentos.this, this.listaEstabelecimentosAux.size() + " registro encontrado.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(ListaEstabelecimentos.this, this.listaEstabelecimentosAux.size() + " registros encontrados.", Toast.LENGTH_LONG).show();
            //this.recyclerView.setVisibility(View.VISIBLE);
            //this.layoutListaEstabelecimentos.removeView(this.layoutListaEstabelecimentos.findViewById(1));
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

        this.listaEstabelecimentos = this.database.getAllEstabelecimentos();
        this.listaEstabelecimentosAux = this.database.getAllEstabelecimentos();

        if (this.listaEstabelecimentos.size() == 0) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("estabelecimentos.csv")));
                String line;
                int i = 1;

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

                    this.listaEstabelecimentos.add(e);
                    i++;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
