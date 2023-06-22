package ai.trueface.samples.livefaceprocessing;

import android.app.Application;

import ai.trueface.sdk.core.ConfigurationOptions;
import ai.trueface.sdk.core.SDK;

public class App extends Application {

    // warning: potential memory leak
    // initializing SDK for first time may take some seconds
    // best done in idle time
    private static SDK sdk;

    @Override
    public void onCreate() {
        super.onCreate();

        ConfigurationOptions options = new ConfigurationOptions();
        // default is false, performance vary depends on phone configuration
        options.enableGPU = false;

        sdk = new SDK(getApplicationContext(), options);
        sdk.setLicense("");  // Setup license key here
    }

    static SDK getSDK() {
        return sdk;
    }


}
