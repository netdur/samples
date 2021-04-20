package ai.trueface.samples.facerecognition;

import android.app.Application;
import android.util.Log;

import ai.trueface.sdk.core.ConfigurationOptions;
import ai.trueface.sdk.core.ErrorCode;
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

        sdk = new SDK(getApplicationContext(), options);
        sdk.setLicense("");

        ErrorCode errorCode = sdk.createDatabaseConnection("fr.db");
        errorCode = sdk.createLoadCollection("collection");
    }

    static SDK getSDK() {
        return sdk;
    }


}
