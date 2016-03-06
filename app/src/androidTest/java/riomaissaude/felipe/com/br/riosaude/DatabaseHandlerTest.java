package riomaissaude.felipe.com.br.riosaude;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.fail;

import riomaissaude.felipe.com.br.riosaude.db.DatabaseHandler;
import riomaissaude.felipe.com.br.riosaude.models.Estabelecimento;
import riomaissaude.felipe.com.br.riosaude.models.StatusEstabelecimento;
import riomaissaude.felipe.com.br.riosaude.utils.LeitorArquivoEstabelecimentos;

/**
 * Testes unitários da camada de persistência.
 *
 * Created by felipe on 3/6/16.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseHandlerTest {

    private DatabaseHandler database;
    private Estabelecimento estabelecimento;
    private LeitorArquivoEstabelecimentos leitorArquivo;

    @Before
    public void setUp() throws Exception {
        getTargetContext().deleteDatabase(DatabaseHandler.DATABASE_NAME);
        this.database = new DatabaseHandler(getTargetContext());
        this.leitorArquivo = new LeitorArquivoEstabelecimentos(getTargetContext());
    }

    @After
    public void tearDown() throws Exception {
        this.database.close();
    }

    @Test
    public void testLerArquivo() {
        try {
            this.leitorArquivo.lerArquivo();
        } catch (IOException e) {
            fail("Teste testLerArquivo falhou.");
        }
    }

    @Test
    public void testAdicionarEstabelecimento() {
        this.estabelecimento = new Estabelecimento
                (1, "1", "", "", "", "Estabelecimento Teste", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");

        this.database.addEstabelecimento(this.estabelecimento);
        List<Estabelecimento> estabelecimentos = this.database.getAllEstabelecimentos();
        assertThat(estabelecimentos.size(), is(1));
    }

    @Test
    public void testAdicionarEstabelecimentos() throws IOException {
        this.leitorArquivo.lerArquivo();

        this.database.addEstabelecimentos(this.leitorArquivo.getEstabelecimentos());

        List<Estabelecimento> estabelecimentos = this.database.getAllEstabelecimentos();
        assertThat(estabelecimentos.size(), is(4466));
    }

    @Test
    public void testAdicionarEstabelecimentosErrado() throws IOException {
        this.leitorArquivo.lerArquivo();

        this.database.addEstabelecimentos(this.leitorArquivo.getEstabelecimentos());

        List<Estabelecimento> estabelecimentos = this.database.getAllEstabelecimentos();
        assertNotEquals(estabelecimentos.size(), is(4465));
    }

    @Test
    public void testGetByPrimaryKey() {
        this.estabelecimento = new Estabelecimento
                (1, "1", "", "", "", "Estabelecimento Teste", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");

        this.database.addEstabelecimento(this.estabelecimento);
        Estabelecimento e = this.database.getByPrimaryKey(1);

        assertThat(e.getNomeFantasia(), is("Estabelecimento Teste"));
    }

    @Test
    public void testFindByNome() {
        this.estabelecimento = new Estabelecimento
                (1, "1", "", "", "", "Estabelecimento Teste", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");

        this.database.addEstabelecimento(this.estabelecimento);
        List<Estabelecimento> estabelecimentos = this.database.findByNome("Estabelecimento Teste");

        assertNotEquals(estabelecimentos.size(), 0);
    }

    @Test
    public void testUpdateStatus() {
        this.estabelecimento = new Estabelecimento
                (1, "1", "", "", "", "Estabelecimento Teste", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", StatusEstabelecimento.SEM_INFORMACAO, "");

        this.database.addEstabelecimento(this.estabelecimento);
        this.database.updateStatusEstabelecimento(1, StatusEstabelecimento.FUNCIONANDO_BEM);

        assertThat(this.database.getByPrimaryKey(1).getStatusEstabelecimento(), is(StatusEstabelecimento.FUNCIONANDO_BEM));
    }

}
