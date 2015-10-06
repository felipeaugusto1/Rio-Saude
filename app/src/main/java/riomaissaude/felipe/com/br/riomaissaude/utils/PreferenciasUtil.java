package riomaissaude.felipe.com.br.riomaissaude.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Classe utilizada para centralizar a inserção e recuperação de dados
 * salvos na memória do aplicativo.
 *
 * Obs: O SharedPreferences é perdido quando o aplicativo é reinstalado ou removido.
 *
 * Created by felipe on 9/30/15.
 */
public class PreferenciasUtil {

    public static final String VALOR_INVALIDO = "_blank";

    public static final String KEY_PREFERENCIAS_DICAS_MAPA = "dicas_mapa";
    public static final String KEY_PREFERENCIAS_DICAS_DETALHE = "dicas_detalhe";

    public static final String KEY_PREFERENCIAS_DIA_ATUAL_VERIFICACAO = "dia_atual";

    public static void salvarPreferencias(String key, String value, Context contexto) {
        SharedPreferences preferencias = contexto.getSharedPreferences(
                key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString(key, value.trim());
        editor.commit();
    }

    public static String getPreferencias(String key, Context contexto) {
        SharedPreferences sharedPreferences = contexto.getSharedPreferences(
                key, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, VALOR_INVALIDO);
    }


}
