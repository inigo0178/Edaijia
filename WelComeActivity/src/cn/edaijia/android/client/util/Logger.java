package cn.edaijia.android.client.util;

import android.util.Log;

public class Logger {

    private static String LOG_TAG = "EDaiJia";
    private static String sClassName;

    private Logger() {

    }

    public static Logger createLogger(String className) {
    	if (className != null) {
    		sClassName = className +": ";
    	} else {
    		sClassName = "";
    	}
        return new Logger();
    }

    public void e(String error) {
        if (Utils.sDebug) {
            Log.e(LOG_TAG, sClassName + error);
        }
    }

    public void e(String error, Throwable throwable) {
        if (Utils.sDebug) {
            Log.e(LOG_TAG, sClassName + error, throwable);
        }
    }

    public void w(String error) {
        if (Utils.sDebug) {
            Log.w(LOG_TAG, sClassName + error);
        }
    }

    public void w(String error, Throwable throwable) {
        if (Utils.sDebug) {
            Log.w(LOG_TAG, sClassName + error, throwable);
        }
    }

    public void i(String msg) {
        if (Utils.sDebug) {
            Log.i(LOG_TAG, sClassName + msg);
        }
    }

    public void i(String msg, Throwable throwable) {
        if (Utils.sDebug) {
            Log.i(LOG_TAG, sClassName + msg, throwable);
        }
    }

    public void d(String msg) {
        if (Utils.sDebug) {
            Log.d(LOG_TAG, sClassName + msg);
        }
    }

    public void d(String msg, Throwable throwable) {
        if (Utils.sDebug) {
            Log.d(LOG_TAG, sClassName + msg, throwable);
        }
    }

    public void v(String msg) {
        if (Utils.sDebug) {
            Log.v(LOG_TAG, sClassName + msg);
        }
    }

    public void v(String msg, Throwable throwable) {
        if (Utils.sDebug) {
            Log.v(LOG_TAG, sClassName + msg, throwable);
        }
    }

}
