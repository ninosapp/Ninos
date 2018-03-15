package in.ninos;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by FAMILY on 20-01-2018.
 */

public class NinosApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
