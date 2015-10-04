package riomaissaude.felipe.com.br.riomaissaude.singleton;

import java.util.ArrayList;
import java.util.List;

import riomaissaude.felipe.com.br.riomaissaude.models.Estabelecimento;

/**
 * Created by felipe on 10/4/15.
 */
public class ListaEstabelecimentosSingleton {

    private static ListaEstabelecimentosSingleton instancia = new ListaEstabelecimentosSingleton();
    private List<Estabelecimento> lista = new ArrayList<Estabelecimento>();

    public static ListaEstabelecimentosSingleton getInstancia() {
        return instancia;
    }

    public List<Estabelecimento> getLista() {
        return this.lista;
    }

    public void setLista(List<Estabelecimento> lista) {
        this.lista = lista;
    }

}
