package riomaissaude.felipe.com.br.riomaissaude.crashlistener;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import vizbom.util.CrashSender;
import android.app.Application;

@ReportsCrashes(formKey = "", mode = ReportingInteractionMode.SILENT)
public class ApplicationCrashListener extends Application {

	@Override
	public void onCreate() {
		ACRA.init(this);
		CrashSender crashSender = new CrashSender();
		ACRA.getErrorReporter().setReportSender(crashSender);
		super.onCreate();
	}
}
