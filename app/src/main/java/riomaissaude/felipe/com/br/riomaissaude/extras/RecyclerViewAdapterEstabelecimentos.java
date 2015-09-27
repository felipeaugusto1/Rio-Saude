package riomaissaude.felipe.com.br.riomaissaude.extras;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import riomaissaude.felipe.com.br.riomaissaude.R;
import riomaissaude.felipe.com.br.riomaissaude.activities.DetalheEstabelecimento;
import riomaissaude.felipe.com.br.riomaissaude.models.Estabelecimento;

/**
 * Created by felipe on 9/13/15.
 */
public class RecyclerViewAdapterEstabelecimentos extends RecyclerView.Adapter<RecyclerViewAdapterEstabelecimentos.EstabelecimentoViewHolder> {

    private List<Estabelecimento> listaEstabelecimentos;

    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;

    public RecyclerViewAdapterEstabelecimentos(List<Estabelecimento> lista) {
        this.listaEstabelecimentos = lista;
    }

    @Override
    public int getItemCount() {
        return listaEstabelecimentos.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public void onBindViewHolder(EstabelecimentoViewHolder ocorrenciaViewHolder, int i) {
        if (!isPositionHeader(i)) {
            Estabelecimento estabelecimento = listaEstabelecimentos.get(i); //-1 eh o cabecalho que foi adicionado

            ocorrenciaViewHolder.id = estabelecimento.getId();
            ocorrenciaViewHolder.nome.setText(estabelecimento.getNomeFantasia());
            ocorrenciaViewHolder.telefone.setText(estabelecimento.getTelefone());
            ocorrenciaViewHolder.logradouro.setText(estabelecimento.getLogradouro());
            ocorrenciaViewHolder.tipoEstabelecimento.setText(estabelecimento.getTipoEstabelecimento());
        }

    }

    @Override
    public EstabelecimentoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = null;
        if (i == TYPE_ITEM)
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card_view_estabelecimento, viewGroup, false);
        else if (i == TYPE_HEADER)
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.recycler_header, viewGroup, false);

        return new EstabelecimentoViewHolder(itemView);
    }

    public static class EstabelecimentoViewHolder extends RecyclerView.ViewHolder {

        protected int id;
        protected TextView nome, telefone, logradouro, tipoEstabelecimento;
        protected View view;

        private Context context;

        public EstabelecimentoViewHolder(View v) {
            super(v);
            view = v;

            nome = (TextView) v.findViewById(R.id.txtcardNome);
            telefone = (TextView) v.findViewById(R.id.txtCardTelefone);
            logradouro = (TextView) v.findViewById(R.id.txtCardLogradouro);
            tipoEstabelecimento = (TextView) v.findViewById(R.id.txtTipoEstabelecimento);

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    Log.d("id clicado", String.valueOf(id));


                    if (id != 0) {
                        context = v.getContext();

                        Intent telaDetalheOcorrencia = new Intent(context,
                                DetalheEstabelecimento.class);

                        telaDetalheOcorrencia.putExtra("estabelecimento_id", String.valueOf(id));
                        //telaDetalheOcorrencia.putExtra("classe", String.valueOf(RecyclerViewAdapterOcorrencias.class));
                        //telaDetalheOcorrencia.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        context.startActivity(telaDetalheOcorrencia);
                    }
                }
            });

        }
    }
}