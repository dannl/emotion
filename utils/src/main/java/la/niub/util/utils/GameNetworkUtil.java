
package la.niub.util.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.FileEntity;
import org.apache.http.util.EntityUtils;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.telephony.TelephonyManager;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import la.niub.util.utils.IoUtils.ProgressListener;

public final class GameNetworkUtil {

    /**
     * MAX_REDIRECT_COUNT
     */
    private static final int MAX_REDIRECT_COUNT = 10;
    /**
     * BUFFER_SIZE
     */
    private static final int BUFFER_SIZE = 4096;
    private static final String TAG = GameNetworkUtil.class.getSimpleName();

    public static boolean isConnected(Context context) {
        final ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    /**
     * 检测网络连接是否可用
     *
     * @param ctx
     * @return true 可用; false 不可用
     */
    public static boolean isNetworkAvailable1(final Context ctx) {
        final ConnectivityManager cm = (ConnectivityManager)ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        final NetworkInfo[] netinfo = cm.getAllNetworkInfo();
        if (netinfo == null) {
            return false;
        }
        for (int i = 0; i < netinfo.length; i++) {
            if (netinfo[i].isConnected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测网络是否存在
     *
     * @param context
     *            上下文
     * @return true:存在网络；false：不存在网络
     */
    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            final NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param context
     */
    public static void startWirelessSettings(final Context context) {
        try {
            final String action = "android.settings.WIRELESS_SETTINGS";
            final String packageName = "com.android.settings";
            final String className = "com.android.settings.WirelessSettings";
            final Intent wirelessIntent = new Intent();
            wirelessIntent.setAction(action);
            wirelessIntent.setClassName(packageName, className);
            context.startActivity(wirelessIntent);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        final ConnectivityManager connectivity = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return null;
        }
        return connectivity.getActiveNetworkInfo();
    }

    public static int getNetworkType(Context context) {
        final NetworkInfo activeInfo = getNetworkInfo(context);
        if (activeInfo == null) {
            return -1;
        }
        return activeInfo.getType();
    }

    public static boolean isWifi(Context context) {
        return getNetworkType(context) == ConnectivityManager.TYPE_WIFI;
    }

    public static String getNetworkTypeName(Context context) {
        final NetworkInfo info = getNetworkInfo(context);
        if (info == null) {
            return null;
        }
        String typeName = null;
        final int type = info.getType();
        if (type == ConnectivityManager.TYPE_WIFI) {
            typeName = "wifi";
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            typeName = "2g";
            final int subType = info.getSubtype();
            switch (subType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
                typeName = "2g";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                typeName = "3g";
                break;
            default:
                typeName = "3g";
            }
        }
        return typeName;
    }


    public static boolean uploadFile(String path, String url) {
        boolean uploaded = false;
        AndroidHttpClient httpCient = null;
        try {
            httpCient = AndroidHttpClient.newInstance(Log.getApplicationTag());
            final HttpPost request = new HttpPost(url);
            final FileEntity fileEntity = new FileEntity(new File(path), "image/png");
            request.setEntity(fileEntity);
            final HttpResponse response = httpCient.execute(request);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                uploaded = true;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (httpCient != null) {
                httpCient.close();
            }
        }
        return uploaded;
    }

    public static boolean downloadFile(String url, String saveTo, ProgressListener listener) {
        return downloadFile(url, saveTo, false, listener);
    }

    public static boolean downloadFile(String url, String saveTo, boolean resume) {
        return downloadFile(url, saveTo, resume, null);
    }

    public static boolean downloadFile(String url, String saveTo, boolean resume, final ProgressListener listener) {
        final File saveToFile = new File(saveTo);
        boolean downloaded = false;
        OutputStream os = null;
        InputStream is = null;
        AndroidHttpClient httpCient = null;
        try {
            httpCient = AndroidHttpClient.newInstance("Starfield Game/1.0");
            HttpClientParams.setRedirecting(httpCient.getParams(), true);
            HttpGet request = new HttpGet(url);
            long bytesDownloaded = 0;
            if (saveToFile.exists()) {
                if (!resume) {
                    saveToFile.delete();
                } else {
                    bytesDownloaded = saveToFile.length();
                    request.addHeader("Range", String.format("bytes=%d-", bytesDownloaded));
                }
            }
            final HttpResponse response = httpCient.execute(request);
            final int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
            case HttpStatus.SC_OK:
            case HttpStatus.SC_PARTIAL_CONTENT:
                os = new FileOutputStream(saveTo, true);
                is = response.getEntity().getContent();
                final long startBytes = bytesDownloaded;
                final long bytesToDownload = response.getEntity().getContentLength();
                if (listener != null) {
                    listener.progress(startBytes, bytesToDownload + startBytes);
                }
                IoUtils.copyStream(is, os, bytesToDownload, new ProgressListener() {

                    @Override
                    public void progress(long current, long total) {
                        if (listener != null) {
                            listener.progress(current + startBytes, total + startBytes);
                        }
                    }
                });
                os.flush();
                downloaded = true;
                break;
            default:
                Log.e(TAG, "HTTP Status=%d", statusCode);
                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    Log.e(EntityUtils.toString(response.getEntity()));
                }
                break;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            closeSliently(is);
            closeSliently(os);
            if (httpCient != null) {
                httpCient.close();
            }
        }
        return downloaded;
    }

    public static boolean downloadFile(String url, String saveTo) {
        return downloadFile(url, saveTo, false, null);
    }

    public static void closeSliently(Closeable closeable)  {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

}
