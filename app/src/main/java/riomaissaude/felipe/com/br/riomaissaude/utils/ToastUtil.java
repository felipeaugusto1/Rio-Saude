package riomaissaude.felipe.com.br.riomaissaude.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by felipe on 8/29/15.
 */
public class ToastUtil {

    public static void criarToastCurto(Context context, String texto) {
        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show();
    }

    public static void criarToastLongo(Context context, String texto) {
        Toast.makeText(context, texto, Toast.LENGTH_LONG).show();
    }

    public static void criarToastCurtoCentralizado(Context context, String texto) {
        Toast toast= Toast.makeText(context,
                texto, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    public static void criarToastLongoCentralizado(Context context, String texto) {
        Toast toast= Toast.makeText(context,
                texto, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
}
