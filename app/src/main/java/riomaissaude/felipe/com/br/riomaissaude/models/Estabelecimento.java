package riomaissaude.felipe.com.br.riomaissaude.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by felipe on 9/13/15.
 */
public class Estabelecimento implements ClusterItem {

    private int id;
    private String media;
    private String cnes;
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private String telefone;
    private String fax;
    private String email;
    private String latitude;
    private String longitude;
    private String dataAtualizacaoCoordenadas;
    private String codigoEsferaAdministrativa;
    private String esferaAdministrativa;
    private String codigoDaAtividade;
    private String atividadeDestino;
    private String codigoNaturezaOrganizacao;
    private String naturezaOrganizacao;
    private String tipoUnidade;
    private String tipoEstabelecimento;
    private String statusEstabelecimento;

    public Estabelecimento() {
    }

    public String getCnes() {
        return cnes;
    }

    public void setCnes(String cnes) {
        this.cnes = cnes;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDataAtualizacaoCoordenadas() {
        return dataAtualizacaoCoordenadas;
    }

    public void setDataAtualizacaoCoordenadas(String dataAtualizacaoCoordenadas) {
        this.dataAtualizacaoCoordenadas = dataAtualizacaoCoordenadas;
    }

    public String getCodigoEsferaAdministrativa() {
        return codigoEsferaAdministrativa;
    }

    public void setCodigoEsferaAdministrativa(String codigoEsferaAdministrativa) {
        this.codigoEsferaAdministrativa = codigoEsferaAdministrativa;
    }

    public String getEsferaAdministrativa() {
        return esferaAdministrativa;
    }

    public void setEsferaAdministrativa(String esferaAdministrativa) {
        this.esferaAdministrativa = esferaAdministrativa;
    }

    public String getCodigoDaAtividade() {
        return codigoDaAtividade;
    }

    public void setCodigoDaAtividade(String codigoDaAtividade) {
        this.codigoDaAtividade = codigoDaAtividade;
    }

    public String getAtividadeDestino() {
        return atividadeDestino;
    }

    public void setAtividadeDestino(String atividadeDestino) {
        this.atividadeDestino = atividadeDestino;
    }

    public String getCodigoNaturezaOrganizacao() {
        return codigoNaturezaOrganizacao;
    }

    public void setCodigoNaturezaOrganizacao(String codigoNaturezaOrganizacao) {
        this.codigoNaturezaOrganizacao = codigoNaturezaOrganizacao;
    }

    public String getNaturezaOrganizacao() {
        return naturezaOrganizacao;
    }

    public void setNaturezaOrganizacao(String naturezaOrganizacao) {
        this.naturezaOrganizacao = naturezaOrganizacao;
    }

    public String getTipoUnidade() {
        return tipoUnidade;
    }

    public void setTipoUnidade(String tipoUnidade) {
        this.tipoUnidade = tipoUnidade;
    }

    public String getTipoEstabelecimento() {
        return tipoEstabelecimento;
    }

    public void setTipoEstabelecimento(String tipoEstabelecimento) {
        this.tipoEstabelecimento = tipoEstabelecimento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getStatusEstabelecimento() {
        return statusEstabelecimento;
    }

    public void setStatusEstabelecimento(String statusEstabelecimento) {
        this.statusEstabelecimento = statusEstabelecimento;
    }

    @Override
    public String toString() {
        return "Estabelecimento{" +
                "media='" + media + '\'' +
                "cnes='" + cnes + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", razaoSocial='" + razaoSocial + '\'' +
                ", nomeFantasia='" + nomeFantasia + '\'' +
                ", logradouro='" + logradouro + '\'' +
                ", numero='" + numero + '\'' +
                ", complemento='" + complemento + '\'' +
                ", bairro='" + bairro + '\'' +
                ", cep='" + cep + '\'' +
                ", telefone='" + telefone + '\'' +
                ", fax='" + fax + '\'' +
                ", email='" + email + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", dataAtualizacaoCoordenadas='" + dataAtualizacaoCoordenadas + '\'' +
                ", codigoEsferaAdministrativa='" + codigoEsferaAdministrativa + '\'' +
                ", esferaAdministrativa='" + esferaAdministrativa + '\'' +
                ", codigoDaAtividade='" + codigoDaAtividade + '\'' +
                ", atividadeDestino='" + atividadeDestino + '\'' +
                ", codigoNaturezaOrganizacao='" + codigoNaturezaOrganizacao + '\'' +
                ", naturezaOrganizacao='" + naturezaOrganizacao + '\'' +
                ", tipoUnidade='" + tipoUnidade + '\'' +
                ", tipoEstabelecimento='" + tipoEstabelecimento + '\'' +
                '}';
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(Double.parseDouble(this.latitude), Double.parseDouble(this.longitude));
    }
}
