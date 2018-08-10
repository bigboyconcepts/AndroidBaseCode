/*
 * Copyright (c) 2017.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.androidbasecode;

import android.app.Application;

import com.loopj.android.http.AsyncHttpClient;

import ke.co.toshngure.basecode.logging.BeeLog;
import ke.co.toshngure.basecode.rest.Client;
import ke.co.toshngure.basecode.rest.ResponseDefinition;
import ke.co.toshngure.camera2.Camera2App;


/**
 * Created by Anthony Ngure on 24/11/2017.
 * Email : anthonyngure25@gmail.com.
 */

public class App extends Camera2App {

    private static App mInstance;

    public static App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BeeLog.init(true, null);
        Client.init(new Client.Config() {
            @Override
            protected Application getContext() {
                return App.this;
            }

            @Override
            protected String getBaseUrl() {
                return "https://toshngure.co.ke/basecode/public/api/v1";
            }

            @Override
            protected String getToken() {
                return null;
            }

            @Override
            protected ResponseDefinition getResponseDefinition() {
                return null;
            }

        });
        mInstance = this;
    }

    @Override
    protected AsyncHttpClient getClient() {
        return Client.getInstance().getClient();
    }
}
