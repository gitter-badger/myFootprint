package com.bosch.myfootprint;

import android.app.Application;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.FlybitsOptions;
import com.flybits.core.api.context.ContextPriority;

/**
 * Created by BHY1MTP on 11/9/2016.
 */

public class FootPrintApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //mooch.init(this, "e29ad8yfyhaq9i7mz9gx99qac");
        FlybitsOptions options = new FlybitsOptions.Builder(this)
                .setDebug(true)
                //    .enablePushNotifications(FlybitsOptions.GCMType.WITHOUT_GOOGLE_SERVICES_JSON, Constants.GCM_APP_ID) //Replace with your own GCM APP ID
                .enableContextUploading(1, ContextPriority.LOW)
                .build();

        Flybits.include(this).initialize(options);
    }
}
