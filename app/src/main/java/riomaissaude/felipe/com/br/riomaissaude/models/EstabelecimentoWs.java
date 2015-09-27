package riomaissaude.felipe.com.br.riomaissaude.models;

/**
 * Created by felipe on 9/25/15.
 */
public class EstabelecimentoWs {

    private Integer id;
    private int qtdVotos;
    private int somaVotos;
    private double media;

    public EstabelecimentoWs(Integer id, int qtdVotos, int somaVotos, double media) {
        this.id = id;
        this.qtdVotos = qtdVotos;
        this.somaVotos = somaVotos;
        this.media = media;
    }

    public EstabelecimentoWs() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getQtdVotos() {
        return qtdVotos;
    }

    public void setQtdVotos(int qtdVotos) {
        this.qtdVotos = qtdVotos;
    }

    public int getSomaVotos() {
        return somaVotos;
    }

    public void setSomaVotos(int somaVotos) {
        this.somaVotos = somaVotos;
    }

    public double getMedia() {
        return media;
    }

    public void setMedia(double media) {
        this.media = media;
    }

    @Override
    public String toString() {
        return "EstabelecimentoWs{" +
                "id=" + id +
                ", qtdVotos=" + qtdVotos +
                ", somaVotos=" + somaVotos +
                ", media=" + media +
                '}';
    }
}
