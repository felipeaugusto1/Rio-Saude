package riomaissaude.felipe.com.br.riomaissaude.crashlistener;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import riomaissaude.felipe.com.br.riomaissaude.utils.WebService;

/**
 * Classe responsável por enviar um erro do aplicativo no dispositivo, para o WS.
 */
public class CrashSender implements ReportSender {

	@Override
	public void send(CrashReportData report) throws ReportSenderException {
		enviarStackTrace(report.getProperty(ReportField.STACK_TRACE));
		enviarVersao(report.getProperty(ReportField.APP_VERSION_NAME));
		enviarCelular("Cel model: " +report.getProperty(ReportField.PHONE_MODEL)+". Android version: " +android.os.Build.VERSION.RELEASE);
	}

	private void enviarVersao(String descricao) {
		RequestParams parametros = new RequestParams();
		parametros.put("descricao", descricao);

		SyncHttpClient client = new SyncHttpClient();
		client.get(WebService.ENDERECO_WS + "estabelecimento/versao", parametros,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] response) {
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] errorResponse, Throwable e) {
					}
				});
	}
	
	private void enviarCelular(String descricao) {
		RequestParams parametros = new RequestParams();
		parametros.put("descricao", descricao);

		SyncHttpClient client = new SyncHttpClient();
		client.get(WebService.ENDERECO_WS + "estabelecimento/modelPhone", parametros,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] response) {
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] errorResponse, Throwable e) {
					}
				});
	}

	private void enviarStackTrace(String descricao) {
		RequestParams parametros = new RequestParams();
		parametros.put("descricao", descricao);

		SyncHttpClient client = new SyncHttpClient();
		client.get(WebService.ENDERECO_WS + "estabelecimento/stackTrace", parametros,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] response) {
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] errorResponse, Throwable e) {
					}
				});
	}

}
