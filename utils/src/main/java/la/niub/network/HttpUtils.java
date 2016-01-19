package la.niub.network;

import android.text.TextUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import la.niub.util.utils.IOUtilities;
import la.niub.util.utils.Log;
import la.niub.util.utils.StringUtil;

public class HttpUtils {

    public interface Decryptor {
        public String onDecrypt(final String encrypted);
    }

    private final static String TAG = "HttpUtils";

    /**
     * GZip compression format.
     */
    protected static final String COMPRESS_FORMAT_GZIP = "gzip";
    /**
     * Deflate compression format.
     */
    protected static final String COMPRESS_FORMAT_DEFLATE = "deflate";

    /**
     * BUFFER_SIZE
     */
    public static final int BUFFER_SIZE = 8192;

    public static void consume(final HttpRequestResult res) {

        if (res != null) {
            consume(res.entity);
        }
    }

    public static void consume(final HttpEntity entity) {
        if (entity == null) {
            return;
        }

        try {
            entity.consumeContent();
        }catch (IOException e) {
            Log.e(TAG, e);
        }
    }

    /**
     * Decode the {@linkplain HttpEntity} as a {@linkplain JSONArray}.
     * @param entity the {@linkplain HttpEntity} to decode.
     * @return the {@linkplain JSONArray} result, or null.
     * @throws IOException
     * @throws JSONException
     */
    public static final JSONArray decodeEntityAsJSONArray(final HttpEntity entity) throws IOException, JSONException {
        final String json = decodeEntityAsString(entity);
        consume(entity);
        if (!TextUtils.isEmpty(json)) {
//            TraceLog trace = TraceLog.start("Parse json");
            JSONArray jsonArray = null;
            jsonArray = new JSONArray(json);
//            trace.end();
            return jsonArray;
        } else {
            return null;
        }

    }

    /**
     * Decode the {@linkplain HttpEntity} as a {@linkplain JSONObject}.
     * @param entity the {@linkplain HttpEntity} to decode.
     * @return the {@linkplain JSONObject} result, or null.
     * @throws JSONException
     * @throws IOException
     */
    public static final JSONObject decodeEntityAsJSONObject(final HttpEntity entity) throws JSONException, IOException {
        return decodeEntityAsJSONObject(entity, null);
    }

    public static final JSONObject decodeEntityAsJSONObject(final HttpEntity entity, final Decryptor decryptor) throws JSONException, IOException {
        final String json = decodeEntityAsString(entity);
        consume(entity);
        final String decrypted;
        if(decryptor != null) {
            decrypted = decryptor.onDecrypt(json);
        } else {
            decrypted = json;
        }
        JSONObject jsonObject = null;
        if(!TextUtils.isEmpty(decrypted)){
            try {
                jsonObject = new JSONObject(decrypted);
            } catch (Exception e) {
                Log.w(e);
                return null;
            }
        }
        return jsonObject;
    }

    /**
     * Decode string from entity.
     * @param entity the entity to decode.
     * @return the string decoded from the entity.
     * @throws IOException
     * @throws
     */
    public static String decodeEntityAsString(final HttpEntity entity) throws IOException {
//        TraceLog trace = TraceLog.start("Get network content as string");
        String result = null;
        if (entity != null) {
            final Header contentEncoding = entity.getContentEncoding();
            Log.d(TAG, "Response encoding = %s.", contentEncoding);

            InputStream inputStream = getEntityStream(entity);
            if (inputStream == null) {
                return null;
            }
            String charset = EntityUtils.getContentCharSet(entity);
            result = StringUtil.stringFromInputStream2(inputStream, charset);
        }

        consume(entity);
//        trace.end();
        return result;
    }

    public static InputStream getEntityStream(HttpEntity entity) throws IOException {
        final Header contentEncoding = entity.getContentEncoding();
        Log.d(TAG, "Response encoding = %s.", contentEncoding);
        InputStream inputStream = null;
        String encoding = null;
        if (contentEncoding != null) {
            encoding = contentEncoding.getValue();
            if (COMPRESS_FORMAT_GZIP.equalsIgnoreCase(encoding)) {
                Log.d(TAG, "Wrapping result with gzip encoding.");
                inputStream = new GZIPInputStream(entity.getContent(), BUFFER_SIZE);
            } else if (COMPRESS_FORMAT_DEFLATE.equalsIgnoreCase(encoding)) {
                Log.d(TAG, "Wrapping result with deflate encoding.");
                inputStream = new InflaterInputStream(entity.getContent());
            }
        }
        else {
            inputStream = entity.getContent();
        }

        return inputStream;
    }

    public static void checkResult(HttpRequestResult result) throws IOException {
        final int statusCode = result.status.getStatusCode();
        final String statusString = result.status.getReasonPhrase();
        if (result.status.getStatusCode() >= 400) {
            String content = decodeEntityAsString(result.entity);
            Log.e(TAG, String.format("Http Error %d, content %s", statusCode, content));
            throw new HttpResponseException(statusCode, statusString);
        }
    }

    public static class HttpRequestResult {
        public HttpResponse response;
        public StatusLine status;
        public HttpEntity entity;

        public HttpRequestResult(HttpResponse response) {
            this.response = response;
            this.status  = response.getStatusLine();
            this.entity = response.getEntity();
        }
    }

    public static HttpRequestResult httpGet(String url) throws IOException {
        return httpGet(url, null);
    }

    public static HttpRequestResult httpGet(String url, String tag) throws IOException {
        HttpRequester requester = new HttpRequester.Builder(url)
                                                   .Category(tag)
                                                   .KeepAlive(false)
                                                   .build();

        return requester.request();
    }

    public static boolean downloadFile(String url, File saveToFile, long limitLength, boolean keepAlive, String tag, Map<String, String> header) {
        if (TextUtils.isEmpty(url)) {
            return true;
        }
        if (null == saveToFile) {
            throw new IllegalArgumentException("saveToFile may not be null.");
        }
        if (saveToFile.exists()) {
            if (!saveToFile.delete()) {
                Log.w("delete file failed");
            }
        }
        FileOutputStream fos = null;
        HttpRequestResult res = null;
        try {
            Header[] parmHeaders = null;
            if (header != null) {
                final Set<Entry<String, String>> headerSet = header.entrySet();
                parmHeaders = new Header[headerSet.size()];
                int index = 0;
                for (Entry<String, String> entry : headerSet) {
                    parmHeaders[index] = new BasicHeader(entry.getKey(), entry.getValue());
                    index ++;
                }
            }
            HttpRequester requester = new HttpRequester.Builder(url)
                                                       .Category(tag)
                                                       .KeepAlive(keepAlive)
                                                       .Headers(parmHeaders)
                                                       .build();

            res = requester.request(keepAlive); // use connection manager if keep alive
            final HttpResponse response = res.response;

            Header[] headers = response.getHeaders("Content-Length");
            if (headers.length > 0) {
                Header lengthHeader = headers[0];
                String lengthString = lengthHeader.getValue();
                if (!TextUtils.isEmpty(lengthString)) {
                    long length = Long.valueOf(lengthString);
                    if (length > limitLength) {
                        Log.e(TAG, "Big Image Load failed:  " + length
                                + " limtLength: " + limitLength);
                        return false;
                    }
                }
            }

            final int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                // get response String
                fos = new FileOutputStream(saveToFile);
                response.getEntity().writeTo(fos);
                return true;
            } else {
                Log.e(TAG, "server reply error:" + statusCode);
            }
        } catch (final Exception e) {
            Log.e(TAG, "request error " + e.toString());
            // e.printStackTrace();
        } finally {
            IOUtilities.closeStream(fos);
            consume(res);
        }
        return false;
    }

    public static boolean downloadFile(String url, File saveToFile, long limitLength, boolean keepAlive) {
        return downloadFile(url, saveToFile, limitLength, keepAlive, null, null);
    }

    public static byte[] toByteArray(final HttpEntity entity)
            throws IOException {
        if (entity == null) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        }
        InputStream instream = getEntityStream(entity);
        if (instream == null) {
            return new byte[] {};
        }
        if (entity.getContentLength() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "HTTP entity too large to be buffered in memory");
        }
        int i = (int) entity.getContentLength();
        if (i < 0) {
            i = 4096;
        }
        ByteArrayBuffer buffer = new ByteArrayBuffer(i);
        try {
            byte[] tmp = new byte[4096];
            int l;
            while ((l = instream.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
        } finally {
            instream.close();
        }

        consume(entity);

        return buffer.toByteArray();
    }

    public static void saveEntityToFile(HttpEntity entity, File file) throws IOException {
        InputStream inputStream = getEntityStream(entity);
        FileOutputStream outputStream = new FileOutputStream(file);
        IOUtilities.copy(inputStream, outputStream);
        outputStream.flush();
        IOUtilities.closeStream(outputStream);
        consume(entity);
    }

    public static HttpEntity createUrlEncodedFormEntity(String... keyValuePairs) {
        if (null == keyValuePairs) {
            return null;
        }
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("The length of keyValuePairs must be a even number");
        }
        ArrayList<BasicNameValuePair> datas = new ArrayList<BasicNameValuePair>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            datas.add(new BasicNameValuePair(keyValuePairs[i], keyValuePairs[i + 1]));
        }
        try {
            HttpEntity entity = new UrlEncodedFormEntity(datas, "utf-8");
            return entity;
        } catch (UnsupportedEncodingException e) {
            Log.w(e);
        }
        return null;
    }


    public interface HttpRequestRetryEntry {
        public HttpRequestResult requestAndProcessData() throws IOException;
    }

    public static HttpRequestResult requestWithRetry(HttpRequestRetryEntry httpRequestRetryEntry,
            int retryTimes, int retryInterval) {
        HttpRequestResult result = null;
        for (int i = 0; i <= retryTimes; i++) {
            try {
                result = httpRequestRetryEntry.requestAndProcessData();
                break;
            } catch (IOException e) {
                if (i == retryTimes) {
                    break;
                } else {
                    try {
                        Thread.sleep(retryInterval);
                    } catch (InterruptedException e2) {
                        Log.e(TAG, e2);
                    }
                }
            }
        }
        return result;
    }
}
