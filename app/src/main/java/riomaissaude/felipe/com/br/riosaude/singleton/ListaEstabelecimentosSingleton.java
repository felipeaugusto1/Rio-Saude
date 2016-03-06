package riomaissaude.felipe.com.br.riosaude.singleton;

import java.util.ArrayList;
import java.util.List;

import riomaissaude.felipe.com.br.riosaude.models.Estabelecimento;
import riomaissaude.felipe.com.br.riosaude.models.StatusEstabelecimento;
import riomaissaude.felipe.com.br.riosaude.utils.ValidatorUtil;

/**
 * Classe que utiliza o padrão de projeto
 * Singleton. Utiliza a mesma lista de Estabelecimentos por todo o aplicativo,
 * enquanto o aplicativo estiver em memória no dispositivo.
 *
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

    /**
     * Atualiza os status de todos os estabelecimentos para Não Informado.
     */
    public void resetarStatusEstabelecimentos() {
        if (!ValidatorUtil.isNuloOuVazio(this.lista)) {
            for (Estabelecimento e :this.lista) {
                if (!e.getStatusEstabelecimento().equalsIgnoreCase(StatusEstabelecimento.SEM_INFORMACAO))
                    e.setStatusEstabelecimento(StatusEstabelecimento.SEM_INFORMACAO);
            }
        }
    }

}
