package riomaissaude.felipe.com.br.riomaissaude.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

/**
 * Classe que possui alguns métodos úteis para validação de objetos ou view.
 *
 * Created by felipe on 9/13/15.
 */
public class ValidatorUtil {

    /**
     * Verifica se o objeto é nulo ou está vazio.
     * @param objeto
     * @return
     */
    public static boolean isNuloOuVazio(Object objeto) {
        if (objeto == null)
            return true;

        if (objeto instanceof String)
            return (((String) objeto).isEmpty());

        if (objeto instanceof Number)
            return (((Number) objeto).intValue() == 0);

        return false;
    }

    /**
     * Verifica se um campo de entrada de texto está em branco,
     * caseo esteja, será exibido uma mensagem informativa..
     * @param pView
     * @param pMessage
     * @return
     */
    public static boolean validarCampoEmBranco(View pView, String pMessage) {
        if (pView instanceof EditText) {
            EditText edText = (EditText) pView;
            Editable text = edText.getText();
            if (text != null) {
                String strText = text.toString();
                strText = strText.trim();
                if (!TextUtils.isEmpty(strText)) {
                    return true;
                }
            }
            // em qualquer outra condição é gerado um erro
            edText.setError(pMessage);
            edText.setFocusable(true);
            edText.requestFocus();
            return false;
        }
        return false;
    }

}
