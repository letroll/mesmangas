package fr.letroll.mesmangas;

import java.util.List;

import org.acra.annotation.ReportsCrashes;

import android.app.Application;

import com.google.inject.Module;

@ReportsCrashes(formKey = "dDBrRVA2OFgtQXlxclliRHowbUtmUFE6MQ")
public class monApplication extends Application {
	// public class monApplication extends RoboApplication {
	public void onCreate() {
		// The following line triggers the initialization of ACRA
		// ACRA.init(this);
		super.onCreate();
	}

	protected void addApplicationModules(List<Module> modules) {
		modules.add(new MonModule());
	}

}
