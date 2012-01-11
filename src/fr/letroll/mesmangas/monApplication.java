package fr.letroll.mesmangas;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "dDBrRVA2OFgtQXlxclliRHowbUtmUFE6MQ")

public class monApplication extends Application {

    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        super.onCreate();
    }
}
