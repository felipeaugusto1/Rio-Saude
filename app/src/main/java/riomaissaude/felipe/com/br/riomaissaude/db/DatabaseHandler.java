package riomaissaude.felipe.com.br.riomaissaude.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.wifi.WifiConfiguration;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import riomaissaude.felipe.com.br.riomaissaude.models.Estabelecimento;
import riomaissaude.felipe.com.br.riomaissaude.models.EstabelecimentoWs;
import riomaissaude.felipe.com.br.riomaissaude.models.StatusEstabelecimento;
import riomaissaude.felipe.com.br.riomaissaude.utils.StringUtil;

/**
 *
 * Classe responsável por todo o acesso do aplicativo ao banco de dados.
 *
 * Created by felipe on 9/21/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "riomaissaude";

    private static final String TABLE_ESTABELECIMENTO = "estabelecimento";

    private static final String KEY_ESTABELECIMENTO_ID = "id";
    private static final String KEY_ESTABELECIMENTO_MEDIA_VOTACAO = "media";
    private static final String KEY_ESTABELECIMENTO_CNES = "cnes";
    private static final String KEY_ESTABELECIMENTO_CNPJ = "cnpj";
    private static final String KEY_ESTABELECIMENTO_RAZAO_SOCIAL = "razao_social";
    private static final String KEY_ESTABELECIMENTO_NOME_FANTASIA = "nome_fantasia";
    private static final String KEY_ESTABELECIMENTO_LOGRADOURO = "logradouro";
    private static final String KEY_ESTABELECIMENTO_NUMERO = "numero";
    private static final String KEY_ESTABELECIMENTO_COMPLEMENTO = "complemento";
    private static final String KEY_ESTABELECIMENTO_BAIRRO = "bairro";
    private static final String KEY_ESTABELECIMENTO_CEP = "cep";
    private static final String KEY_ESTABELECIMENTO_TELEFONE = "telefone";
    private static final String KEY_ESTABELECIMENTO_FAX = "fax";
    private static final String KEY_ESTABELECIMENTO_EMAIL = "email";
    private static final String KEY_ESTABELECIMENTO_LATITUDE = "latitude";
    private static final String KEY_ESTABELECIMENTO_LONGITUDE = "longitude";
    private static final String KEY_ESTABELECIMENTO_DATA_ATUALIZACAO_COORDENADAS = "data_atualizacao_coord";
    private static final String KEY_ESTABELECIMENTO_CODIGO_ESFERA_ADMINISTRATIVA = "cod_esfera_adm";
    private static final String KEY_ESTABELECIMENTO_ESFERA_ADMINISTRATIVA = "esfera_adm";
    private static final String KEY_ESTABELECIMENTO_CODIGO_DA_ATIVIDADE = "cod_atividade";
    private static final String KEY_ESTABELECIMENTO_ATIVIDADE_DESTINO = "atividade_destino";
    private static final String KEY_ESTABELECIMENTO_CODIGO_NATUREZA_ORGANIZACAO = "cod_natureza_organizacao";
    private static final String KEY_ESTABELECIMENTO_NATUREZA_ORGANIZACAO = "natureza_organizacao";
    private static final String KEY_ESTABELECIMENTO_TIPO_UNIDADE = "tipo_unidade";
    private static final String KEY_ESTABELECIMENTO_TIPO_ESTABELECIMENTO = "tipo_estabelecimento";
    private static final String KEY_ESTABELECIMENTO_STATUS_ESTABELECIMENTO = "status_estabelecimento";
    private static final String KEY_ESTABELECIMENTO_DATA_ALTERACAO_STATUS_ESTABELECIMENTO = "data_alteracao_status_estabelecimento";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ESTABELECIMENTO_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ESTABELECIMENTO + "("
                + KEY_ESTABELECIMENTO_ID + " INTEGER PRIMARY KEY,"
                + KEY_ESTABELECIMENTO_CNES + " TEXT, "
                + KEY_ESTABELECIMENTO_CNPJ + " TEXT, "
                + KEY_ESTABELECIMENTO_RAZAO_SOCIAL + " TEXT, "
                + KEY_ESTABELECIMENTO_NOME_FANTASIA + " TEXT, "
                + KEY_ESTABELECIMENTO_LOGRADOURO + " TEXT, "
                + KEY_ESTABELECIMENTO_NUMERO + " TEXT, "
                + KEY_ESTABELECIMENTO_COMPLEMENTO + " TEXT, "
                + KEY_ESTABELECIMENTO_BAIRRO + " TEXT, "
                + KEY_ESTABELECIMENTO_CEP + " TEXT, "
                + KEY_ESTABELECIMENTO_TELEFONE + " TEXT, "
                + KEY_ESTABELECIMENTO_FAX + " TEXT, "
                + KEY_ESTABELECIMENTO_EMAIL + " TEXT, "
                + KEY_ESTABELECIMENTO_LATITUDE + " TEXT, "
                + KEY_ESTABELECIMENTO_LONGITUDE + " TEXT, "
                + KEY_ESTABELECIMENTO_DATA_ATUALIZACAO_COORDENADAS + " TEXT, "
                + KEY_ESTABELECIMENTO_CODIGO_ESFERA_ADMINISTRATIVA + " TEXT, "
                + KEY_ESTABELECIMENTO_ESFERA_ADMINISTRATIVA + " TEXT, "
                + KEY_ESTABELECIMENTO_CODIGO_DA_ATIVIDADE + " TEXT, "
                + KEY_ESTABELECIMENTO_ATIVIDADE_DESTINO + " TEXT, "
                + KEY_ESTABELECIMENTO_CODIGO_NATUREZA_ORGANIZACAO + " TEXT, "
                + KEY_ESTABELECIMENTO_NATUREZA_ORGANIZACAO + " TEXT, "
                + KEY_ESTABELECIMENTO_TIPO_UNIDADE + " TEXT, "
                + KEY_ESTABELECIMENTO_TIPO_ESTABELECIMENTO + " TEXT, "
                + KEY_ESTABELECIMENTO_MEDIA_VOTACAO + " TEXT, "
                + KEY_ESTABELECIMENTO_STATUS_ESTABELECIMENTO + " TEXT )";

        db.execSQL(CREATE_ESTABELECIMENTO_TABLE);
    }

    /**
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ESTABELECIMENTO);
        onCreate(db);
    }

    /**
     *
     * @param db
     * @param tableName
     */
    private void deletarRegistros(SQLiteDatabase db, String tableName) {
        //db.execSQL("delete from " + tableName);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ESTABELECIMENTO);
        onCreate(db);
    }

    /**
     *
     */
    public void deletarEstabelecimentos() {
        SQLiteDatabase db = this.getWritableDatabase();
        deletarRegistros(db, TABLE_ESTABELECIMENTO);
    }

    /**
     * Atualiza a média e o status de um estabelecimento no banco de dados.
     *
     * @param id
     * @param media
     * @param status
     * @return
     */
    public boolean updateAvaliacaoEstabelecimento(int id, double media, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(KEY_ESTABELECIMENTO_MEDIA_VOTACAO, String.valueOf(media));
        args.put(KEY_ESTABELECIMENTO_STATUS_ESTABELECIMENTO, status);

        String where = KEY_ESTABELECIMENTO_ID + "=" + id;

        return db.update(TABLE_ESTABELECIMENTO, args, where, null) > 0;
    }

    /**
     * Atualiza o status de um estabelecimento no banco de dados.
     *
     * @param id
     * @param status
     * @return
     */
    public boolean updateStatusEstabelecimento(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(KEY_ESTABELECIMENTO_STATUS_ESTABELECIMENTO, status);

        String where = KEY_ESTABELECIMENTO_ID + "=" + id;

        return db.update(TABLE_ESTABELECIMENTO, args, where, null) > 0;
    }

    /**
     * Procurar estabelecimentos dado um nome.
     * Procura pelo tipo do estabelecimento, nome, endereço...
     * @param nome
     * @return
     */
    public List<Estabelecimento> findByNome(String nome) {
        List<Estabelecimento> listaEstabelecimentos = new ArrayList<Estabelecimento>();

        String selectQuery = "SELECT  * FROM " + TABLE_ESTABELECIMENTO + " WHERE "
                + KEY_ESTABELECIMENTO_NOME_FANTASIA +" like '%" +nome+"%'" +
                " OR " + KEY_ESTABELECIMENTO_BAIRRO +" like '%" +nome +"%'" +
                " OR " + KEY_ESTABELECIMENTO_COMPLEMENTO +" like '%" +nome +"%'" +
                " OR " + KEY_ESTABELECIMENTO_LOGRADOURO +" like '%" +nome +"%'" +
                " OR " + KEY_ESTABELECIMENTO_RAZAO_SOCIAL +" like '%" +nome +"%'" +
                " OR " + KEY_ESTABELECIMENTO_TIPO_ESTABELECIMENTO +" like '%" +nome +"%'" +
                " OR " + KEY_ESTABELECIMENTO_ATIVIDADE_DESTINO +" like '%" +nome +"%'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Estabelecimento estabelecimento = new Estabelecimento();
                estabelecimento.setId(Integer.parseInt(cursor.getString(0)));

                estabelecimento.setCnes(cursor.getString(1));
                estabelecimento.setCnpj(cursor.getString(2));
                estabelecimento.setRazaoSocial(cursor.getString(3));
                estabelecimento.setNomeFantasia(cursor.getString(4));
                estabelecimento.setLogradouro(cursor.getString(5));
                estabelecimento.setNumero(cursor.getString(6));
                estabelecimento.setComplemento(cursor.getString(7));
                estabelecimento.setBairro(cursor.getString(8));
                estabelecimento.setCep(cursor.getString(9));
                estabelecimento.setTelefone(cursor.getString(10));
                estabelecimento.setFax(cursor.getString(11));
                estabelecimento.setEmail(cursor.getString(12));
                estabelecimento.setLatitude(cursor.getString(13));
                estabelecimento.setLongitude(cursor.getString(14));
                estabelecimento.setDataAtualizacaoCoordenadas(cursor.getString(15));
                estabelecimento.setCodigoEsferaAdministrativa(cursor.getString(16));
                estabelecimento.setEsferaAdministrativa(cursor.getString(17));
                estabelecimento.setCodigoDaAtividade(cursor.getString(18));
                estabelecimento.setAtividadeDestino(cursor.getString(19));
                estabelecimento.setCodigoNaturezaOrganizacao(cursor.getString(20));
                estabelecimento.setNaturezaOrganizacao(cursor.getString(21));
                estabelecimento.setTipoUnidade(cursor.getString(22));
                estabelecimento.setTipoEstabelecimento(cursor.getString(23));
                estabelecimento.setMedia(cursor.getString(24));
                estabelecimento.setStatusEstabelecimento(cursor.getString(25));

                listaEstabelecimentos.add(estabelecimento);
            } while (cursor.moveToNext());
        }

        return listaEstabelecimentos;
    }

    /**
     * Atualiza o status de todos os estabelecimentos no banco de dados para Não Informado.
     */
    public void resetarStatusEstabelecimentos() {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(KEY_ESTABELECIMENTO_STATUS_ESTABELECIMENTO, StatusEstabelecimento.SEM_INFORMACAO);

        String where = KEY_ESTABELECIMENTO_STATUS_ESTABELECIMENTO + " != '" +StatusEstabelecimento.SEM_INFORMACAO +"'";

        db.update(TABLE_ESTABELECIMENTO, args, where, null);
    }

    /**
     * Inseri uma lista de estabelecimentos no banco de dados.
     *
     * @param estabelecimentos
     */
    public void addEstabelecimentos(List<Estabelecimento> estabelecimentos) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values;

        for (Estabelecimento estabelecimento : estabelecimentos) {
            values = new ContentValues();
            values.put(KEY_ESTABELECIMENTO_ID, estabelecimento.getId());
            values.put(KEY_ESTABELECIMENTO_CNES, estabelecimento.getCnes());
            values.put(KEY_ESTABELECIMENTO_CNPJ, estabelecimento.getCnpj());
            values.put(KEY_ESTABELECIMENTO_RAZAO_SOCIAL, estabelecimento.getRazaoSocial());
            values.put(KEY_ESTABELECIMENTO_NOME_FANTASIA, estabelecimento.getNomeFantasia());
            values.put(KEY_ESTABELECIMENTO_LOGRADOURO, estabelecimento.getLogradouro());
            values.put(KEY_ESTABELECIMENTO_NUMERO, estabelecimento.getNumero());
            values.put(KEY_ESTABELECIMENTO_COMPLEMENTO, estabelecimento.getComplemento());
            values.put(KEY_ESTABELECIMENTO_BAIRRO, estabelecimento.getBairro());
            values.put(KEY_ESTABELECIMENTO_CEP, estabelecimento.getCep());
            values.put(KEY_ESTABELECIMENTO_TELEFONE, estabelecimento.getTelefone());
            values.put(KEY_ESTABELECIMENTO_FAX, estabelecimento.getFax());
            values.put(KEY_ESTABELECIMENTO_EMAIL, estabelecimento.getEmail());
            values.put(KEY_ESTABELECIMENTO_LATITUDE, estabelecimento.getLatitude());
            values.put(KEY_ESTABELECIMENTO_LONGITUDE, estabelecimento.getLongitude());
            values.put(KEY_ESTABELECIMENTO_DATA_ATUALIZACAO_COORDENADAS, estabelecimento.getDataAtualizacaoCoordenadas());
            values.put(KEY_ESTABELECIMENTO_CODIGO_ESFERA_ADMINISTRATIVA, estabelecimento.getCodigoEsferaAdministrativa());
            values.put(KEY_ESTABELECIMENTO_ESFERA_ADMINISTRATIVA, estabelecimento.getEsferaAdministrativa());
            values.put(KEY_ESTABELECIMENTO_CODIGO_DA_ATIVIDADE, estabelecimento.getCodigoDaAtividade());
            values.put(KEY_ESTABELECIMENTO_ATIVIDADE_DESTINO, estabelecimento.getAtividadeDestino());
            values.put(KEY_ESTABELECIMENTO_CODIGO_NATUREZA_ORGANIZACAO, estabelecimento.getCodigoNaturezaOrganizacao());
            values.put(KEY_ESTABELECIMENTO_NATUREZA_ORGANIZACAO, estabelecimento.getNaturezaOrganizacao());
            values.put(KEY_ESTABELECIMENTO_TIPO_UNIDADE, estabelecimento.getTipoUnidade());
            values.put(KEY_ESTABELECIMENTO_TIPO_ESTABELECIMENTO, estabelecimento.getTipoEstabelecimento());
            values.put(KEY_ESTABELECIMENTO_MEDIA_VOTACAO, String.valueOf(0));
            values.put(KEY_ESTABELECIMENTO_STATUS_ESTABELECIMENTO, estabelecimento.getStatusEstabelecimento());

            db.insert(TABLE_ESTABELECIMENTO, null, values);
        }

        db.close();
    }

    public void addEstabelecimento(Estabelecimento estabelecimento) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values;

            values = new ContentValues();
            values.put(KEY_ESTABELECIMENTO_ID, estabelecimento.getId());
            values.put(KEY_ESTABELECIMENTO_CNES, estabelecimento.getCnes());
            values.put(KEY_ESTABELECIMENTO_CNPJ, estabelecimento.getCnpj());
            values.put(KEY_ESTABELECIMENTO_RAZAO_SOCIAL, estabelecimento.getRazaoSocial());
            values.put(KEY_ESTABELECIMENTO_NOME_FANTASIA, estabelecimento.getNomeFantasia());
            values.put(KEY_ESTABELECIMENTO_LOGRADOURO, estabelecimento.getLogradouro());
            values.put(KEY_ESTABELECIMENTO_NUMERO, estabelecimento.getNumero());
            values.put(KEY_ESTABELECIMENTO_COMPLEMENTO, estabelecimento.getComplemento());
            values.put(KEY_ESTABELECIMENTO_BAIRRO, estabelecimento.getBairro());
            values.put(KEY_ESTABELECIMENTO_CEP, estabelecimento.getCep());
            values.put(KEY_ESTABELECIMENTO_TELEFONE, estabelecimento.getTelefone());
            values.put(KEY_ESTABELECIMENTO_FAX, estabelecimento.getFax());
            values.put(KEY_ESTABELECIMENTO_EMAIL, estabelecimento.getEmail());
            values.put(KEY_ESTABELECIMENTO_LATITUDE, estabelecimento.getLatitude());
            values.put(KEY_ESTABELECIMENTO_LONGITUDE, estabelecimento.getLongitude());
            values.put(KEY_ESTABELECIMENTO_DATA_ATUALIZACAO_COORDENADAS, estabelecimento.getDataAtualizacaoCoordenadas());
            values.put(KEY_ESTABELECIMENTO_CODIGO_ESFERA_ADMINISTRATIVA, estabelecimento.getCodigoEsferaAdministrativa());
            values.put(KEY_ESTABELECIMENTO_ESFERA_ADMINISTRATIVA, estabelecimento.getEsferaAdministrativa());
            values.put(KEY_ESTABELECIMENTO_CODIGO_DA_ATIVIDADE, estabelecimento.getCodigoDaAtividade());
            values.put(KEY_ESTABELECIMENTO_ATIVIDADE_DESTINO, estabelecimento.getAtividadeDestino());
            values.put(KEY_ESTABELECIMENTO_CODIGO_NATUREZA_ORGANIZACAO, estabelecimento.getCodigoNaturezaOrganizacao());
            values.put(KEY_ESTABELECIMENTO_NATUREZA_ORGANIZACAO, estabelecimento.getNaturezaOrganizacao());
            values.put(KEY_ESTABELECIMENTO_TIPO_UNIDADE, estabelecimento.getTipoUnidade());
            values.put(KEY_ESTABELECIMENTO_TIPO_ESTABELECIMENTO, estabelecimento.getTipoEstabelecimento());
            values.put(KEY_ESTABELECIMENTO_MEDIA_VOTACAO, String.valueOf(0));
            values.put(KEY_ESTABELECIMENTO_STATUS_ESTABELECIMENTO, estabelecimento.getStatusEstabelecimento());

            db.insert(TABLE_ESTABELECIMENTO, null, values);


        db.close();
    }

    /**
     * Busca todos os bairros distintos no banco de dados.
     *
     * @return
     */
    public List<String> getAllBairros() {
        List<String> bairros = new ArrayList<String>();

        String selectQuery = "SELECT DISTINCT(" + KEY_ESTABELECIMENTO_BAIRRO + ") FROM " + TABLE_ESTABELECIMENTO + " order by " + KEY_ESTABELECIMENTO_BAIRRO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                bairros.add(StringUtil.converterPrimeiraLetraMaiuscula(cursor.getString(0)));
            } while (cursor.moveToNext());
        }

        return bairros;
    }

    /**
     * Busca todos os estabelecimentos no banco de dados.
     *
     * @return
     */
    public List<Estabelecimento> getAllEstabelecimentos() {
        List<Estabelecimento> listaEstabelecimentos = new ArrayList<Estabelecimento>();
        String selectQuery = "SELECT  * FROM " + TABLE_ESTABELECIMENTO;// + " limit 2000";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Estabelecimento estabelecimento = new Estabelecimento();
                estabelecimento.setId(Integer.parseInt(cursor.getString(0)));

                estabelecimento.setCnes(cursor.getString(1));
                estabelecimento.setCnpj(cursor.getString(2));
                estabelecimento.setRazaoSocial(cursor.getString(3));
                estabelecimento.setNomeFantasia(cursor.getString(4));
                estabelecimento.setLogradouro(cursor.getString(5));
                estabelecimento.setNumero(cursor.getString(6));
                estabelecimento.setComplemento(cursor.getString(7));
                estabelecimento.setBairro(cursor.getString(8));
                estabelecimento.setCep(cursor.getString(9));
                estabelecimento.setTelefone(cursor.getString(10));
                estabelecimento.setFax(cursor.getString(11));
                estabelecimento.setEmail(cursor.getString(12));
                estabelecimento.setLatitude(cursor.getString(13));
                estabelecimento.setLongitude(cursor.getString(14));
                estabelecimento.setDataAtualizacaoCoordenadas(cursor.getString(15));
                estabelecimento.setCodigoEsferaAdministrativa(cursor.getString(16));
                estabelecimento.setEsferaAdministrativa(cursor.getString(17));
                estabelecimento.setCodigoDaAtividade(cursor.getString(18));
                estabelecimento.setAtividadeDestino(cursor.getString(19));
                estabelecimento.setCodigoNaturezaOrganizacao(cursor.getString(20));
                estabelecimento.setNaturezaOrganizacao(cursor.getString(21));
                estabelecimento.setTipoUnidade(cursor.getString(22));
                estabelecimento.setTipoEstabelecimento(cursor.getString(23));
                estabelecimento.setMedia(cursor.getString(24));
                estabelecimento.setStatusEstabelecimento(cursor.getString(25));

                listaEstabelecimentos.add(estabelecimento);
            } while (cursor.moveToNext());
        }

        return listaEstabelecimentos;
    }

    /**
     * Busca um estabelecimento com um determinado id no banco de dados.
     *
     * @param id
     * @return
     */
    public Estabelecimento getByPrimaryKey(int id) {
        String selectQuery = "SELECT  * FROM " + TABLE_ESTABELECIMENTO + " WHERE "
                + KEY_ESTABELECIMENTO_ID + " = " + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Estabelecimento estabelecimento = new Estabelecimento();
        if (cursor.moveToFirst()) {
            do {
                estabelecimento.setId(Integer.parseInt(cursor.getString(0)));

                estabelecimento.setCnes(cursor.getString(1));
                estabelecimento.setCnpj(cursor.getString(2));
                estabelecimento.setRazaoSocial(cursor.getString(3));
                estabelecimento.setNomeFantasia(cursor.getString(4));
                estabelecimento.setLogradouro(cursor.getString(5));
                estabelecimento.setNumero(cursor.getString(6));
                estabelecimento.setComplemento(cursor.getString(7));
                estabelecimento.setBairro(cursor.getString(8));
                estabelecimento.setCep(cursor.getString(9));
                estabelecimento.setTelefone(cursor.getString(10));
                estabelecimento.setFax(cursor.getString(11));
                estabelecimento.setEmail(cursor.getString(12));
                estabelecimento.setLatitude(cursor.getString(13));
                estabelecimento.setLongitude(cursor.getString(14));
                estabelecimento.setDataAtualizacaoCoordenadas(cursor.getString(15));
                estabelecimento.setCodigoEsferaAdministrativa(cursor.getString(16));
                estabelecimento.setEsferaAdministrativa(cursor.getString(17));
                estabelecimento.setCodigoDaAtividade(cursor.getString(18));
                estabelecimento.setAtividadeDestino(cursor.getString(19));
                estabelecimento.setCodigoNaturezaOrganizacao(cursor.getString(20));
                estabelecimento.setNaturezaOrganizacao(cursor.getString(21));
                estabelecimento.setTipoUnidade(cursor.getString(22));
                estabelecimento.setTipoEstabelecimento(cursor.getString(23));
                estabelecimento.setMedia(cursor.getString(24));
                estabelecimento.setStatusEstabelecimento(cursor.getString(25));
            } while (cursor.moveToNext());
        }

        return estabelecimento;
    }

}
