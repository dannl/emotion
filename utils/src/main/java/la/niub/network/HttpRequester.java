package la.niub.network;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import la.niub.util.utils.Log;

public class HttpRequester {

    private static final String TAG = "HttpRequester";
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private static final int INVALID_TIME_OUT = -1;
    private static final int SOCKET_BUUFER_SIZE = 8192;         // 8k
    private static final int SOCKET_TIMEOUT = 60 * 1000;        // 60 seconds
    private static final int CONNECTION_TIMEOUT = 20 * 1000;    // 20 seconds
    private static final int CONNECTION_MAX_IDLE = 15;          // 15 seconds
    private static final int IDLE_CHECK_INTERVAL = 5 * 1000;    // 5 seconds
    private static final int DEFAULT_ALIVE_DURATION = 15 * 1000;// 15 seconds

    private static HttpParams sDefaultHttpParams;
    private static SchemeRegistry sDefaultSchemeRegistry;
    private static SchemeRegistry sNoSSLCertificateCheckSchemeRegistry;
    private static ClientConnectionManager sDefaultClientConnectionManager;
    private static IdleConnectionMonitorThread sCleanupThread;
    private static HttpExtensionParams sHttpExtensionParams;

    static final class AutoKeepAliveStrategy extends DefaultConnectionKeepAliveStrategy {

        /*
         * (non-Javadoc)
         * @see org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy#
         * getKeepAliveDuration(org.apache.http.HttpResponse,
         * org.apache.http.protocol.HttpContext)
         */
        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            long duration = super.getKeepAliveDuration(response, context);
            if (duration <= 0) {
                duration = DEFAULT_ALIVE_DURATION;
            }
            return duration;
        }
    }

    public static final class AutoHttpClient extends DefaultHttpClient {

        AutoHttpClient() {
            super();
        }

        AutoHttpClient(HttpParams params) {
            super(params);
        }

        public AutoHttpClient(ClientConnectionManager manager, HttpParams params) {
            super(manager, params);
        }

        /*
         * (non-Javadoc)
         * @see org.apache.http.impl.client.DefaultHttpClient#
         * createConnectionKeepAliveStrategy()
         */
        @Override
        protected ConnectionKeepAliveStrategy createConnectionKeepAliveStrategy() {
            return new AutoKeepAliveStrategy();
        }
    }

    public static class IdleConnectionMonitorThread extends Thread {
        private final ClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(ClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(IDLE_CHECK_INTERVAL);
                        connMgr.closeExpiredConnections();
                        connMgr.closeIdleConnections(CONNECTION_MAX_IDLE, TimeUnit.SECONDS);
                    }
                }
            } catch (final InterruptedException ex) {
                // Terminated by user, just ignore.
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    private static HttpParams createDefaultHttpParams() {
        final HttpParams params = new BasicHttpParams();

        // Increase the connection count for the connection manager.
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(4));

        // Turn off stale checking. Our connections break all the time anyway,
        // and it's not worth it to pay the penalty of checking every time.
        HttpConnectionParams.setStaleCheckingEnabled(params, false);

        // Default connection and socket timeout of 20 seconds. Tweak to taste.
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(params, SOCKET_BUUFER_SIZE);

        HttpProtocolParams.setUseExpectContinue(params, false); // android-changed from AndroidHttpClient

        HttpClientParams.setRedirecting(params, true);

        return params;
    }

    private static SchemeRegistry createDefaultSchemeRegistry() {
        final SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        // TODO change it back once we have own signed certificate, this will trust all cert and is not safe
        //schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
        // schemeRegistry.register(new Scheme("https", new MySSLSocketFactory(), 443));
        return schemeRegistry;
    }

    private static SchemeRegistry createNoSSLCertificateCheckSchemeRegistry() {
        final SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
        return schemeRegistry;
    }

    private static ClientConnectionManager createPooledClientConnectionManager() {
        final HttpParams params = getDefaultHttpParams();
        final SchemeRegistry schemeRegistry = getDefaultSchemeRegistry();
        final ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
        if (sCleanupThread != null) {
            sCleanupThread.shutdown();
        }
        sCleanupThread = new IdleConnectionMonitorThread(manager);
        return manager;
    }

    public static void init(HttpExtensionParams p) {

        sHttpExtensionParams = p;
    }

    public static HttpExtensionParams getHttpExtensionParams() {

        if (sHttpExtensionParams == null) {
            sHttpExtensionParams = new HttpExtensionParams();
        }
        return sHttpExtensionParams;
    }

    public static HttpParams getDefaultHttpParams() {
        if (null == sDefaultHttpParams) {
            sDefaultHttpParams = createDefaultHttpParams();
        }
        return sDefaultHttpParams;
    }

    public static SchemeRegistry getDefaultSchemeRegistry() {
        if (null == sDefaultSchemeRegistry) {
            sDefaultSchemeRegistry = createDefaultSchemeRegistry();
        }
        return sDefaultSchemeRegistry;
    }

    private static SchemeRegistry getNoSSLCertificateCheckSchemeRegistry() {
        if (null == sNoSSLCertificateCheckSchemeRegistry) {
            sNoSSLCertificateCheckSchemeRegistry = createNoSSLCertificateCheckSchemeRegistry();
        }
        return sNoSSLCertificateCheckSchemeRegistry;
    }

    public static ClientConnectionManager getPooledClientConnectionManager() {
        if (null == sDefaultClientConnectionManager) {
            ClientConnectionManager manager = createPooledClientConnectionManager();
            sDefaultClientConnectionManager = manager;
        }
        return sDefaultClientConnectionManager;
    }

    public static ClientConnectionManager getBasicClientConnectionManager(boolean checkSSLCertificate) {
        // return a new ClientConnectionManager for each call,
        // SingleClientConnManager cannot be cached.
        final HttpParams params = getDefaultHttpParams();
        final SchemeRegistry schemeRegistry;
        if (checkSSLCertificate) {
            schemeRegistry = getDefaultSchemeRegistry();
        } else {
            schemeRegistry = getNoSSLCertificateCheckSchemeRegistry();
        }
        ClientConnectionManager manager = new SingleClientConnManager(params, schemeRegistry);
        return manager;
    }

    private static HttpClient createHttpClient(boolean useConnectionManager, boolean checkSSLCertificate) {
        final HttpClient client;
        HttpParams params = getDefaultHttpParams();
        if (!checkSSLCertificate) {
            client = new DefaultHttpClient(getBasicClientConnectionManager(false), params);
        } else if (useConnectionManager) {
            client = new AutoHttpClient(getPooledClientConnectionManager(), params);
        } else {
            client = new DefaultHttpClient(getBasicClientConnectionManager(true), params);
        }

        HttpProtocolParams.setUserAgent(params, getHttpExtensionParams().getDefaultUserAgent());

        return client;
    }

    private final HttpRequesterData mData;

    private HttpRequester(HttpRequesterData data) {
        mData = data;
    }

    public static void shutdown() {
        if (sCleanupThread != null) {
            sCleanupThread.shutdown();
        }
        if (sDefaultClientConnectionManager != null) {
            sDefaultClientConnectionManager.shutdown();
            sDefaultClientConnectionManager = null;
        }
    }

    public final HttpUtils.HttpRequestResult request() throws IOException {
        return request(false);
    }

    public HttpUtils.HttpRequestResult request(boolean useConnectionManager) throws IOException {
        final HttpClient client = createHttpClient(useConnectionManager, mData.mCheckSSLCertificate);
        HttpRequestBase request;
        String url = mData.mUrl;

        String[] urlParameters = mData.mUrlParameters;
        if (urlParameters != null) {
            Uri.Builder builder = Uri.parse(mData.mUrl).buildUpon();
            for (int i = 0; i < urlParameters.length; i += 2) {
                builder.appendQueryParameter(urlParameters[i], urlParameters[i + 1]);
            }
            url = builder.toString();
        }

        if (mData.mEntity != null
                || HttpPost.METHOD_NAME.equalsIgnoreCase(mData.mMethod)) {
            request = new HttpPost(url);
        } else if (HttpPut.METHOD_NAME.equalsIgnoreCase(mData.mMethod)) {
            request = new HttpPut(url);
        } else if (HttpDelete.METHOD_NAME.equalsIgnoreCase(mData.mMethod)) {
            request = new HttpDelete(url);
        } else if (HttpHead.METHOD_NAME.equalsIgnoreCase(mData.mMethod)) {
            request = new HttpHead(url);
        } else {
            request = new HttpGet(url);
        }
        if (request instanceof HttpEntityEnclosingRequestBase) {
            HttpEntityEnclosingRequestBase r = ((HttpEntityEnclosingRequestBase)request);
            // Disable the f**king expectation
            r.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            r.setEntity(mData.mEntity);
        }

        if (mData.mHeaders != null && mData.mHeaders.length != 0) {
            for (final Header header : mData.mHeaders) {
                request.addHeader(header);
            }
        }

        if (mData.mInterceptRequest != null) {
            mData.mInterceptRequest.intercept(request);
        }

        request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);

        // keep alive on SingleClientConnManager makes little sense.
        if (mData.mKeepAlive && useConnectionManager) {
            request.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        } else {
            request.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
        }

        HttpParams params = client.getParams();

        if (!TextUtils.isEmpty(mData.mUserAgent)) {
            HttpProtocolParams.setUserAgent(params, mData.mUserAgent);
        }

        if (mData.mSocketTimeout != INVALID_TIME_OUT) {
            HttpConnectionParams.setSoTimeout(params, mData.mSocketTimeout);
        }

        if (mData.mConnectTimeout != INVALID_TIME_OUT) {
            HttpConnectionParams.setConnectionTimeout(params, mData.mConnectTimeout);
        }

        HttpClientParams.setRedirecting(params, mData.mRedirect);

        try {
            Log.d(TAG, "HTTP %s to %s", request.getMethod(), request.getURI());
            final HttpResponse response = client.execute(request);
            return new HttpUtils.HttpRequestResult(response);
        } catch (OutOfMemoryError ex) {
            // Do not use IOException(ex), it's available since API 9
            throw createException(ex);
        }catch (Exception e) {
            throw createException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static final IOException createException(Throwable e) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return new IOException(e);
        } else {
            e.printStackTrace();
            return new IOException(e.getMessage());
        }
    }

    private static class HttpRequesterData {
        private String mUrl;
        private HttpEntity mEntity;
        private Header[] mHeaders;
        private String[] mUrlParameters;
        private String mCategory;
        private String mMethod;
        private String mUserAgent;
        private InterceptRequest mInterceptRequest;
        private boolean mKeepAlive = false; // by default disable it
        private boolean mCheckSSLCertificate = true; // by default always check ssl certificate
        private int mSocketTimeout = INVALID_TIME_OUT;
        private int mConnectTimeout = INVALID_TIME_OUT;
        private boolean mRedirect = true;
    }

    public static class Builder {
        private final HttpRequesterData mData;

        public Builder(String url) {
            mData = new HttpRequesterData();
            mData.mUrl = url;
        }

        public Builder Entity(HttpEntity entity) {
            mData.mEntity = entity;
            return this;
        }

        public Builder Headers(Header[] headers) {
            mData.mHeaders = headers;
            return this;
        }

        public Builder UrlParameters(String... keyValues) {
            if (keyValues != null && keyValues.length % 2 != 0) {
                throw new IllegalArgumentException("argment keyValues must like this key1, value1, key2, value2, ...");
            }
            mData.mUrlParameters = keyValues;
            return this;
        }

        public Builder InterceptRequest(InterceptRequest ir) {
            mData.mInterceptRequest = ir;
            return this;
        }

        public Builder Method(String method) {
            mData.mMethod = method;
            return this;
        }

        public Builder KeepAlive(boolean keepAlive) {
            mData.mKeepAlive = keepAlive;
            return this;
        }

        public Builder UserAgent(String userAgent) {
            mData.mUserAgent = userAgent;
            return this;
        }

        public Builder Category(String category) {
            mData.mCategory = category;
            return this;
        }

        public Builder CheckSSLCertificate(boolean checkSSLCertificate) {
            mData.mCheckSSLCertificate = checkSSLCertificate;
            return this;
        }

        public Builder SoTimeout(int timeout) {
            mData.mSocketTimeout = timeout;
            return this;
        }

        public Builder ConnectTimeout(int timeout) {
            mData.mConnectTimeout = timeout;
            return this;
        }

        public Builder Redirect(boolean redirect) {
            mData.mRedirect = redirect;
            return this;
        }

        public HttpRequester build() {
            return new HttpRequester(mData);
        }
    }

    public String getUrl() {
        if (mData != null) {
            return mData.mUrl;
        }
        return null;
    }

    public static interface InterceptRequest {
        void intercept(HttpRequestBase request);
    }

    public static UrlEncodedFormEntity createUrlEncodedFormEntity(String key, String value, String... keyValues) {
        if (keyValues != null && keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("argment keyValues must like this key2, value2, key3, value3, ...");
        }
        ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair(key, value));
        if (keyValues != null && keyValues.length > 0) {
            for (int i = 0; i < keyValues.length; i+=2) {
                list.add(new BasicNameValuePair(keyValues[i], keyValues[i + 1]));
            }
        }
        try {
            return new UrlEncodedFormEntity(list, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.w(e);
        }
        return null;
    }
}
