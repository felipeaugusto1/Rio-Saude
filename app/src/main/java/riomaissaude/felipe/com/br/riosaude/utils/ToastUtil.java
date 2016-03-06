package riomaissaude.felipe.com.br.riosaude.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Classe criada para centralizar e simplificar a exibição de mensagens (Toast)
 * na tela para o usuário. Pode ser criado mensagens longas ou curtas.
 *
 * Created by felipe on 8/29/15.
 */
public class ToastUtil {

    /**
     *
     * Criar toast curto.
     *
     * @param context
     * @param texto
     */
    public static void criarToastCurto(Context context, String texto) {
        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * Criar toast longo.
     *
     * @param context
     * @param texto
     */
    public static void criarToastLongo(Context context, String texto) {
        Toast.makeText(context, texto, Toast.LENGTH_LONG).show();
    }

    /**
     *
     * Criar toast curto centralizado na tela.
     *
     * @param context
     * @param texto
     */
    public static void criarToastCurtoCentralizado(Context context, String texto) {
        Toast toast= Toast.makeText(context,
                texto, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    /**
     *
     * Criar toast longo centralizado na tela.
     *
     * @param context
     * @param texto
     */
    public static void criarToastLongoCentralizado(Context context, String texto) {
        Toast toast= Toast.makeText(context,
                texto, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
}
