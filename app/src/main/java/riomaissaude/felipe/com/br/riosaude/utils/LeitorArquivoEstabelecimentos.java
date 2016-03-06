package riomaissaude.felipe.com.br.riosaude.utils;


import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import riomaissaude.felipe.com.br.riosaude.models.Estabelecimento;
import riomaissaude.felipe.com.br.riosaude.models.StatusEstabelecimento;

/**
 * Classe responsável por realizar a leitura do arquivo estabelecimentos.csv.
 *
 * Created by felipe on 3/6/16.
 */
public class LeitorArquivoEstabelecimentos {

    private List<Estabelecimento> estabelecimentos;
    private Context contexto;

    public LeitorArquivoEstabelecimentos(Context contexto) {
        this.contexto = contexto;
        this.estabelecimentos = new ArrayList<>();
    }

    public void lerArquivo() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.contexto.getAssets().open("estabelecimentos.csv")));
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


            this.estabelecimentos.add(e);
        }
    }

    public List<Estabelecimento> getEstabelecimentos() {
        return estabelecimentos;
    }
}
