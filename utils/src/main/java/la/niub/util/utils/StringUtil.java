/*******************************************************************************
 *
 *    Copyright (c) Niub Info Tech Co. Ltd
 *
 *    Snappy
 *
 *    StringUtil
 *
 *    @author: chzhong
 *    @since:  2010-6-9
 *    @version: 1.0
 *
 ******************************************************************************/

package la.niub.util.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本工具类。
 *
 * @author chzhong
 *
 */
public class StringUtil {

    public static final String ENCODING_UNICODE = "Unicode";
    public static final String ENCODING_UTF_8 = "UTF-8";
    public static final String ENCODING_ASCII = "ASCII";

    public static final int MAX_COMMENT_LENGTH = 140;
    public static final int MAX_PASSWORD_LENGTH = 100;
    public static final int MIN_PASSWORD_LENGTH = 6;
    private static final Pattern NICK_NAME_PATTERN = Pattern.compile("^[_A-Za-z][_A-Za-z0-9]{2,14}$",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern NICK_NAME_RESERVED_PATTERN = Pattern.compile(
            ".*Tapler.*|.*Admin.*|.*System.*|.*Mobo\\W*Tap.*|.*Mobo\\W*Square.*", Pattern.CASE_INSENSITIVE
                    | Pattern.UNICODE_CASE);

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private static final long ONE_GB = 1L << 30;
    private static final long ONE_MB = 1L << 20;
    private static final long ONE_KB = 1L << 10;

    private static final long GB_UNIT_THREDHOLD = 1000000000;
    private static final long MB_UNIT_THREDHOLD = 1000000;
    private static final long KB_UNIT_THREDHOLD = 1000;

    private static final String GB_UNIT = "G";
    private static final String MB_UNIT = "M";
    private static final String KB_UNIT = "K";
    private static final String BYTES_UNIT = "Byte(s)";

    private static final String SIZE_FORMAT = "%,.2f %s";
    private static final String SIZE_FORMAT_WITHOUT_DIGIT = "%,.0f %s";

    /**
     * The max length for file name.
     */
    public static final int MAX_PATH = 256;

    /**
     * Illegal file name chars.
     */
    public static final Pattern ILLEGAL_FILE_NAME_CHARS = Pattern.compile("[\\\\/:*?<>|]+");

    /**
     * 空白字符。
     */
    private static final char[] WhitespaceChars = new char[] { '\u0000', '\u0001', '\u0002', '\u0003', '\u0004',
            '\u0005', '\u0006', '\u0007', '\u0008', '\u0009', '\n', '\u000b', '\u000c', '\r', '\u000e', '\u000f',
            '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019',
            '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', '\u0020', '　'

    };
    private static final String TAG = "StringUtil";

    private static final int BUFFER_SIZE = 4096;

    /**
     * 判断给定的字符数组（已排序）中是否包含给定的字符。
     *
     * @param chars
     *            已经排序的字符数组。
     * @param ch
     *            要检查的字符。
     * @return 如果 ch 存在于 chars 中则返回 true；否则返回 false。
     */
    public static final boolean containsChar(final char[] chars, final char ch) {
        return Arrays.binarySearch(chars, ch) >= 0;
    }

    /**
     * 返回参数中第一个非 {@code null} 的参数。
     *
     * @param args
     *            要检查的参数。
     * @return 给定的参数中第一个非 {@code null} 的参数；如果参数都为 {@code null}，则返回 {@code null}。
     */
    public static <T> T firstNotNull(final T... args) {
        for (final T object : args) {
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    /**
     * 返回参数中第一个非 {@code null} 的参数。
     *
     * @param args
     *            要检查的参数。
     * @return 给定的参数中第一个非 {@code null} 的参数；如果参数都为 {@code null}，则返回 {@code null}。
     */
    public static <T extends CharSequence> T firstNotEmpty(final T... args) {
        for (final T object : args) {
            if (!TextUtils.isEmpty(object)) {
                return object;
            }
        }
        return null;
    }

    /**
     * 返回参数中第一个非零的参数。
     *
     * @param args
     *            要检查的参数。
     * @return 给定的参数中第一个非零的参数；如果参数都为零，则返回零。
     */
    public static int firstNonZeroInt(final int... args) {
        for (final int object : args) {
            if (object != 0) {
                return object;
            }
        }
        return 0;
    }

    /**
     * 返回参数中第一个非负数的参数。
     *
     * @param args
     *            要检查的参数。
     * @return 给定的参数中第一个非负数；如果参数都为负数，则返回 {@linkplain Integer#MIN_VALUE}。
     */
    public static int firstNonNegativeInt(final int... args) {
        for (final int object : args) {
            if (object >= 0) {
                return object;
            }
        }
        return Integer.MIN_VALUE;
    }

    /**
     * 返回参数中第一个正数的参数。
     *
     * @param args
     *            要检查的参数。
     * @return 给定的参数中第一个正数；如果参数都为负数或零，则返回零。
     */
    public static int firstPostiveInt(final int... args) {
        for (final int object : args) {
            if (object > 0) {
                return object;
            }
        }
        return 0;
    }

    /**
     * 返回参数中第一个非零的参数。
     *
     * @param args
     *            要检查的参数。
     * @return 给定的参数中第一个非零的参数；如果参数都为零，则返回零。
     */
    public static long firstNonZeroLong(final long... args) {
        for (final long object : args) {
            if (object != 0) {
                return object;
            }
        }
        return 0;
    }

    /**
     * 返回参数中第一个非负数的参数。
     *
     * @param args
     *            要检查的参数。
     * @return 给定的参数中第一个非负数；如果参数都为负数，则返回 {@linkplain longeger#MIN_VALUE}。
     */
    public static long firstNonNegativeLong(final long... args) {
        for (final long object : args) {
            if (object >= 0) {
                return object;
            }
        }
        return Long.MIN_VALUE;
    }

    /**
     * 返回参数中第一个正数的参数。
     *
     * @param args
     *            要检查的参数。
     * @return 给定的参数中第一个正数；如果参数都为负数或零，则返回零。
     */
    public static long firstPostiveLong(final long... args) {
        for (final long object : args) {
            if (object > 0) {
                return object;
            }
        }
        return 0;
    }

    /**
     * 返回参数中第一个非零的参数。
     *
     * @param args
     *            要检查的参数。
     * @return 给定的参数中第一个非零的参数；如果参数都为零，则返回零。
     */
    public static double firstNonZero(final double... args) {
        for (final double object : args) {
            if (object != 0) {
                return object;
            }
        }
        return 0;
    }

    /**
     * 返回参数中第一个数的参数。
     *
     * @param args
     *            要检查的参数。
     * @return 给定的参数中第一个数；如果参数都为 {@linkplain Double#NaN}，则返回 {@linkplain Double#NaN}。
     */
    public static double firstDouble(final double... args) {
        for (final double object : args) {
            if (!Double.isNaN(object)) {
                return object;
            }
        }
        return Double.NaN;
    }

    /**
     * 返回参数中第一个有穷数的参数。
     *
     * @param args
     *            要检查的参数。
     * @return 给定的参数中第一个有穷数；如果参数都为 {@linkplain Double#NaN} 或者无穷，则返回 {@linkplain Double#NaN}。
     */
    public static double firstFinite(final double... args) {
        for (final double object : args) {
            if (!Double.isNaN(object) && !Double.isInfinite(object)) {
                return object;
            }
        }
        return Double.NaN;
    }

    /**
     * 返回参数中第一个非负数的参数。
     *
     * @param args
     *            要检查的参数。
     * @return 给定的参数中第一个非负数；如果参数都为负数，则返回 {@linkplain Double#NaN}。
     */
    public static double firstNonNegative(final double... args) {
        for (final double object : args) {
            if (object >= 0) {
                return object;
            }
        }
        return Double.NaN;
    }

    /**
     * 返回参数中第一个正数的参数。
     *
     * @param args
     *            要检查的参数。
     * @return 给定的参数中第一个正数；如果参数都为负数或零，则返回零。
     */
    public static double firstPostive(final double... args) {
        for (final double object : args) {
            if (object > 0) {
                return object;
            }
        }
        return 0;
    }

    /**
     * 将给定的时间格式化为 XSD/SOAP 的格式。
     *
     * @param date
     *            要格式化的日期。
     * @return 该日期的 yyyy-MM-ddTHH:mm:ss+ZZ:ZZ 的格式。
     * @deprecated 请使用 {@link com.mobosquare.util.mgeek.android.util.XmlUtil#dateToXsd(Date)} ，它提供了相同的功能。 方法。
     */
    @Deprecated
    public static final String formatDate(final Date date) {
        final String dateTime = String.format("%1$tFT%1$tT", date);
        final String zone = String.format("%1$tz", date);
        return dateTime + zone.substring(0, 3) + ":" + zone.substring(3);
    }

    /**
     * 获取一个类完全限定名的简单名。
     *
     * @param className
     *            类的完全限定名。
     * @return className 的简单名。
     */
    public static final String getSimpleClassName(final String className) {
        if (isNullOrEmpty(className)) {
            return className;
        }
        final int index = className.lastIndexOf('.');
        if (-1 == index) {
            return className.substring(index);
        }
        return className;
    }

    /**
     * 获取异常信息的堆栈跟踪信息。
     *
     * @param throwable
     *            要解析的异常信息。
     * @return 该异常信息的堆栈跟踪信息。
     */
    public static final String getThrowableStackTrace(final Throwable throwable) {
        final StringWriter sWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(sWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        printWriter.close();
        final String message = sWriter.toString();
        return message;
    }

    /**
     * 获取给定字符串中，某些特定字符的第一个索引。
     *
     * @param value
     *            要搜索的字符串。
     * @param chars
     *            要查找的字符。
     * @return value 中第一个出现的 chars 中任意字符的索引。 如果 value 中不含有 chars 中的任意字符，则返回 -1。
     */
    public static int indexOfAny(final String value, final char... chars) {
        return indexOfAny(value, 0, chars);
    }

    /**
     * 获取给定字符串中，某些特定字符的第一个索引。
     *
     * @param value
     *            要搜索的字符串。
     * @param chars
     *            要查找的字符。
     * @param start
     *            查找的起始点。
     * @return value 中，从 start 起第一个出现的 chars 中任意字符的索引。 如果 value 中不含有 chars 中的任意字符，则返回 -1。
     */
    public static int indexOfAny(final String value, final int start, final char... chars) {
        if (null == value || value.length() == 0) {
            return -1;
        }
        int i = 0;
        final int n = chars.length;
        int index = -1;
        while (i < n && -1 == index) {
            index = value.indexOf(chars[i], start);
            ++i;
        }
        return index;
    }

    /**
     * 判断一个字符串是否为 ""。
     *
     * @param 待检查的字符串
     *            。
     * @return 如果字符串为 ""，则返回 true；否则返回 false。
     */
    public static final boolean isEmpty(final String value) {
        return null != value && 0 == value.length();
    }

    /**
     * 判断一个字符串是否为 null 或者 ""。
     *
     * @param 待检查的字符串
     *            。
     * @return 如果字符串为 null 或者 ""，则返回 true；否则返回 false。
     */
    public static final boolean isNullOrEmpty(final String value) {
        return TextUtils.isEmpty(value) || "null".equalsIgnoreCase(value);
    }

    /**
     * 判断一个字符串是否为 null 或者只包含空白字符（空格、回车、换行等）。
     *
     * @param 待检查的字符串
     *            。
     * @return 如果字符串为 null 或者只包含空白字符，则返回 true；否则返回 false。
     */
    public static final boolean isNullOrWhitespaces(final String value) {
        return null == value || 0 == value.trim().length();
    }

    /**
     * 判断一个字符串是否只包含空白字符（空格、回车、换行等）。
     *
     * @param 待检查的字符串
     *            。
     * @return 如果字符串只包含空白字符，则返回 true；否则返回 false。
     */
    public static final boolean isWhitespaces(final String value) {
        return null != value && 0 == value.trim().length();
    }

    public static final boolean isWhitespaceCharSequence(CharSequence chars) {
        if (chars == null || chars.length() == 0) {
            return true;
        }
        for (int i = chars.length() - 1; i >= 0; i--) {
            char c = chars.charAt(i);
            if (!containsChar(WhitespaceChars, c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取给定字符串中，某些特定字符的最后一个索引。
     *
     * @param value
     *            要搜索的字符串。
     * @param chars
     *            要查找的字符。
     * @return value 中最后一个出现的 chars 中任意字符的索引。 如果 value 中不含有 chars 中的任意字符，则返回 -1。
     */
    public static final int lastIndexOfAny(final String value, final char... chars) {
        return lastIndexOfAny(value, value.length() - 1, chars);
    }

    /**
     * 获取给定字符串中，某些特定字符的最后一个索引。
     *
     * @param value
     *            要搜索的字符串。
     * @param chars
     *            要查找的字符。
     * @param start
     *            查找的起始点，从这个索引开始向前搜索。
     * @return value 中，从 start 起最后一个出现的 chars 中任意字符的索引。 如果 value 中不含有 chars 中的任意字符，则返回 -1。
     */
    public static final int lastIndexOfAny(final String value, final int start, final char... chars) {
        if (null == value || value.length() == 0) {
            return -1;
        }
        int i = 0;
        int n = chars.length;
        int index = -1;
        while (i < n && -1 == index) {
            index = value.lastIndexOf(chars[i], start);
            ++i;
        }
        return index;
    }

    /**
     * 规范化字符串。
     *
     * @param s
     *            要规范化的字符串。
     * @return 如果字符串为 null 或者长度大于零，则返回 s；如果字符串长度为零，则返回 null。
     */
    public final static String normalize(final String s) {
        if (s == null || s.length() > 0) {
            return s;
        }
        return null;
    }

    /**
     * 以 UTF-8 编码读取一个 Assert 文件的内容。
     *
     * @param context
     *            当前应用程序的上下文。
     * @param fileName
     *            要读取的 Assert 文件名。
     * @return 如果成功，返回 Assert 文件的内容；否则返回 null。
     */
    public static String stringFromAssert(final Context context, final String fileName) {
        String content = null;
        try {
            final AssetManager asserts = context.getAssets();
            final InputStream myInput = asserts.open(fileName);
            final byte[] boe = new byte[3];
            int bytes = myInput.read(boe, 0, boe.length);
            if (bytes < boe.length && !BitUtil.boeIsUtf8(boe)) {
                myInput.reset();
            }
            content = StringUtil.stringFromInputStream(myInput, ENCODING_UTF_8);
            myInput.close();
        } catch (final Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error reading string from assert.");
        }
        return content;
    }

    /**
     * 以给定的编码读取一个 Assert 文件的内容。
     *
     * @param context
     *            当前应用程序的上下文。
     * @param fileName
     *            要读取的 Assert 文件名。
     * @param encoding
     *            文件的编码方式。如果文件有 BOE，则根据 BOE 确定编码。
     * @return 如果成功，返回 Assert 文件的内容；否则返回 null。
     */
    public static String stringFromAssert(final Context context, final String fileName, String encoding) {
        String content = null;
        try {
            final AssetManager asserts = context.getAssets();
            final InputStream myInput = asserts.open(fileName);
            final byte[] boe = new byte[3];
            int bytes = myInput.read(boe, 0, boe.length);
            if (bytes < 3 && BitUtil.boeIsUtf8(boe)) {
                encoding = ENCODING_UTF_8;
                myInput.reset();
            } else if (BitUtil.boeIsUnicode(boe)) {
                encoding = ENCODING_UNICODE;
                myInput.reset();
                myInput.skip(2);
            }
            content = StringUtil.stringFromInputStream(myInput, encoding);
            myInput.close();
        } catch (final Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error reading string from assert.");
        }
        return content;
    }

    /**
     * 读取缓存读取器中的内容。
     *
     * @param bufferedReader
     *            要读取的缓冲读取器。
     * @return 存读取器中的内容。
     */
    public static String stringFromBufferedReader(final BufferedReader bufferedReader) {
        if (bufferedReader == null) {
            return null;
        }
        final StringBuffer result = new StringBuffer();
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
        return result.toString();
    }

    /**
     * 读取一个输入流中的内容，并构造成字符串。
     *
     * @param inputStream
     *            要读取的输入流。
     * @param encoding
     *            流采用的字符编码。
     * @return 流中内容所表示的字符串。
     * @throws UnsupportedEncodingException
     *             不支持此编码。
     */
    public static String stringFromInputStream(final InputStream inputStream, final String encoding)
            throws UnsupportedEncodingException {
        final InputStreamReader reader = new InputStreamReader(inputStream, encoding);
        return stringFromBufferedReader(new BufferedReader(reader));
    }

    public static String stringFromReader(final Reader reader) throws UnsupportedEncodingException {
        return stringFromBufferedReader(new BufferedReader(reader));
    }

    /**
     * 移除给定字符串开头和结尾处的空白字符。
     *
     * @param value
     *            要修剪的字符串。
     * @return 修剪后的字符串。
     */
    public static String trim(final String value) {
        return trim(value, WhitespaceChars);
    }

    /**
     * 移除给定字符串开头和结尾处的特定字符。
     *
     * @param value
     *            要修剪的字符串。
     * @param chars
     *            要移除的字符。
     * @return 修剪后的字符串。
     */
    public static String trim(final String value, final char... chars) {
        if (null == value || value.length() == 0) {
            return value;
        }
        Arrays.sort(chars);
        int startIndex = 0;
        int endIndex = value.length() - 1;
        boolean flag = containsChar(chars, value.charAt(startIndex));
        while (flag && startIndex <= endIndex) {
            startIndex++;
            flag = containsChar(chars, value.charAt(startIndex));
        }

        flag = containsChar(chars, value.charAt(endIndex));
        while (flag && startIndex <= endIndex) {
            endIndex--;
            flag = containsChar(chars, value.charAt(endIndex));
        }
        if (startIndex >= endIndex) {
            return "";
        }
        return value.substring(startIndex, endIndex + 1);
    }

    /**
     * 移除给定字符串结尾的空白字符。
     *
     * @param value
     *            要修剪的字符串。
     * @return 修剪后的字符串。
     */
    public static final String trimEnd(final String value) {
        return trimEnd(value, WhitespaceChars);
    }

    /**
     * 移除给定字符串结尾处的特定字符。
     *
     * @param value
     *            要修剪的字符串。
     * @param chars
     *            要移除的字符。
     * @return 修剪后的字符串。
     */
    public static String trimEnd(final String value, final char... chars) {
        if (null == value || value.length() == 0) {
            return value;
        }
        Arrays.sort(chars);
        int endIndex = value.length() - 1;
        boolean flag = containsChar(chars, value.charAt(endIndex));
        while (flag) {
            endIndex--;
            flag = containsChar(chars, value.charAt(endIndex));
        }
        if (0 >= endIndex) {
            return "";
        }
        return value.substring(0, endIndex + 1);
    }

    /**
     * 移除给定字符串开头的空白字符。
     *
     * @param value
     *            要修剪的字符串。
     * @return 修剪后的字符串。
     */
    public static final String trimStart(final String value) {
        return trimStart(value, WhitespaceChars);
    }

    /**
     * 移除给定字符串开头的特定字符。
     *
     * @param value
     *            要修剪的字符串。
     * @param chars
     *            要移除的字符。
     * @return 修剪后的字符串。
     */
    public static String trimStart(final String value, final char... chars) {
        if (null == value || value.length() == 0) {
            return value;
        }
        Arrays.sort(chars);
        int startIndex = 0;
        boolean flag = containsChar(chars, value.charAt(startIndex));
        while (flag) {
            startIndex++;
            flag = containsChar(chars, value.charAt(startIndex));
        }
        if (startIndex >= value.length()) {
            return "";
        }
        return value.substring(startIndex);
    }

    public static final Pattern URL_PATTERN = Pattern
            .compile("(http://[a-zA-Z0-9+&@#/%?=~_\\-|!:\\.]*?)([^a-zA-Z0-9+&@#/%?=~_\\-|!:\\.]|$)");

    StringUtil() {
    }

    /**
     * Convert an {@link InputStream} to String.
     *
     * @param stream
     *            the stream that contains data.
     * @param encoding
     *            the encoding of the data.
     * @return the result string.
     * @throws IOException
     *             an I/O error occurred.
     */
    public static String stringFromInputStream2(final InputStream stream, String encoding) throws IOException {
        if (null == stream) {
            throw new IllegalArgumentException("stream may not be null.");
        }
        if (TextUtils.isEmpty(encoding)) {
            encoding = System.getProperty("file.encoding", "utf-8");
        }
        String result;
        final InputStreamReader reader = new InputStreamReader(stream, encoding);
        final StringWriter writer = new StringWriter();
        final char[] buffer = new char[BUFFER_SIZE];
        int charRead = reader.read(buffer);
        while (charRead > 0) {
            writer.write(buffer, 0, charRead);
            charRead = reader.read(buffer);
        }
        result = writer.toString();
        return result;
    }

    /**
     * Convert an {@link InputStream} to String.
     *
     * @param stream
     *            the stream that contains data.
     * @return the result string.
     * @throws IOException
     *             an I/O error occurred.
     */
    public static String stringFromInputStream2(final InputStream stream) throws IOException {
        return stringFromInputStream2(stream, "utf-8");
    }

    /**
     * Tiny a given string, return a valid file name.
     *
     * @param fileName
     *            the file name to clean.
     * @return the valid file name.
     */
    public static String safeFileName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }
        if (fileName.length() > MAX_PATH) {
            fileName = fileName.substring(0, MAX_PATH);
        }
        final Matcher matcher = ILLEGAL_FILE_NAME_CHARS.matcher(fileName);
        fileName = matcher.replaceAll("_");
        return fileName;
    }

    /**
     * Html-encode the string.
     *
     * @param s
     *            the string to be encoded
     * @return the encoded string
     */
    public static String htmldecode(String text) {
        text = text.replaceAll("&amp;", "&");
        text = text.replaceAll("&lt;", "<");
        text = text.replaceAll("&gt;", ">");
        text = text.replaceAll("&quot;", "\"");
        text = text.replaceAll("&apos;", "\'");
        return text;
    }

    public static final String optString(final JSONObject object, final String key) {
        final Object o = object.opt(key);
        return !JSONObject.NULL.equals(o) ? o.toString().replaceAll("^\\s+\n", "") : null;
    }

    /**
     * Error message presented when a user tries to treat an opaque URI as hierarchical.
     */
    private static final String NOT_HIERARCHICAL = "This isn't a hierarchical URI.";

    /**
     * Get query parameter from {@linkplain Uri} correctly.
     *
     * @param uri
     *            the {@linkplain Uri}
     * @param key
     *            the query key.
     * @return the decode query value of with key, or {@code null} if not found.
     */
    public static final String getQueryParameter(final Uri uri, final String key) {
        if (null == uri) {
            throw new IllegalArgumentException("uri");
        }
        if (uri.isOpaque()) {
            throw new UnsupportedOperationException(NOT_HIERARCHICAL);
        }
        if (key == null) {
            throw new IllegalArgumentException("key");
        }

        final String query = uri.getEncodedQuery();
        if (query == null) {
            return null;
        }

        final String encodedKey = Uri.encode(key, null);
        final int encodedKeyLength = encodedKey.length();

        int encodedKeySearchIndex = 0;
        final int encodedKeySearchEnd = query.length() - (encodedKeyLength + 1);

        while (encodedKeySearchIndex <= encodedKeySearchEnd) {
            final int keyIndex = query.indexOf(encodedKey, encodedKeySearchIndex);
            if (keyIndex == -1) {
                break;
            }
            final int equalsIndex = keyIndex + encodedKeyLength;
            if (equalsIndex >= query.length()) {
                break;
            }
            if (query.charAt(equalsIndex) != '=') {
                encodedKeySearchIndex = equalsIndex + 1;
                continue;
            }
            if (keyIndex == 0 || query.charAt(keyIndex - 1) == '&') {
                int end = query.indexOf('&', equalsIndex);
                if (end == -1) {
                    end = query.length();
                }
                try {
                    return URLDecoder.decode(query.substring(equalsIndex + 1, end), ENCODING_UTF_8);
                } catch (final UnsupportedEncodingException e) {
                    // We never get here.
                    return null;
                }
            } else {
                encodedKeySearchIndex = equalsIndex + 1;
            }
        }
        return null;
    }

    /**
     * Searches the query string for parameter values with the given key.
     *
     * @param key
     *            which will be encoded
     *
     * @throws UnsupportedOperationException
     *             if this isn't a hierarchical URI
     * @throws IllegalArgumentException
     *             if key is null
     *
     * @return a list of decoded values
     */
    public List<String> getQueryParameters(Uri uri, String key) {
        if (null == uri) {
            throw new IllegalArgumentException("uri");
        }
        if (uri.isOpaque()) {
            throw new UnsupportedOperationException(NOT_HIERARCHICAL);
        }

        String query = uri.getEncodedQuery();
        if (query == null) {
            return Collections.emptyList();
        }

        String encodedKey;
        try {
            encodedKey = URLEncoder.encode(key, ENCODING_UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }

        // Prepend query with "&" making the first parameter the same as the
        // rest.
        query = "&" + query;

        // Parameter prefix.
        final String prefix = "&" + encodedKey + "=";

        final ArrayList<String> values = new ArrayList<String>();

        int start = 0;
        final int length = query.length();
        while (start < length) {
            start = query.indexOf(prefix, start);

            if (start == -1) {
                // No more values.
                break;
            }

            // Move start to start of value.
            start += prefix.length();

            // Find end of value.
            int end = query.indexOf('&', start);
            if (end == -1) {
                end = query.length();
            }

            final String value = query.substring(start, end);
            try {
                values.add(URLDecoder.decode(value, ENCODING_UTF_8));
            } catch (final UnsupportedEncodingException e) {
                throw new AssertionError(e);
            }

            start = end;
        }

        return Collections.unmodifiableList(values);
    }

    public static String extractLink(CharSequence message) {
        String result = null;
        if (!TextUtils.isEmpty(message)) {
            Matcher matcher = URL_PATTERN.matcher(message);
            if (matcher.find()) {
                result = matcher.group(1);
            }
        }

        // This handle some special case for twitter,
        // that the url get truncated at the end.
        // In this case we are not able to get the correct url.
        if (!TextUtils.isEmpty(result)) {
            if (message.toString().endsWith(result + " ...")) {
                result = null;
            }
        }

        try {
            if (!TextUtils.isEmpty(result)) {
                new URL(result);
                // Log.i("extractLink", result);
            }
        } catch (MalformedURLException e) {
            result = null;
        }

        return result;
    }

    private static class HtmlCharSequence implements Spanned {
        private String mHtml;
        private Spanned mCharSequence;

        /**
         * Initiate a new instance of {@link HtmlCharSequence}.
         *
         * @param html
         * @param charSequence
         */
        public HtmlCharSequence(String html, Spanned charSequence) {
            super();
            mHtml = html;
            mCharSequence = charSequence;
        }

        @Override
        public int length() {
            return mCharSequence.length();
        }

        @Override
        public char charAt(int index) {
            return mCharSequence.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return mCharSequence.subSequence(start, end);
        }

        @Override
        public <T> T[] getSpans(int start, int end, Class<T> type) {
            return mCharSequence.getSpans(start, end, type);
        }

        @Override
        public int getSpanStart(Object tag) {
            return mCharSequence.getSpanStart(tag);
        }

        @Override
        public int getSpanEnd(Object tag) {
            return mCharSequence.getSpanEnd(tag);
        }

        @Override
        public int getSpanFlags(Object tag) {
            return mCharSequence.getSpanFlags(tag);
        }

        @Override
        public int nextSpanTransition(int start, int limit, Class type) {
            return mCharSequence.nextSpanTransition(start, limit, type);
        }

        @Override
        public String toString() {
            return mCharSequence.toString();
        }

        /**
         * Retrieve the html.
         *
         * @return the html
         */
        public String getHtml() {
            return mHtml;
        }

    }

    public static final String EMPTY_STRING = "";

    public static String toString(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return EMPTY_STRING;
        }
        if (charSequence instanceof HtmlCharSequence) {
            return ((HtmlCharSequence) charSequence).getHtml();
        }
        return charSequence.toString();
    }

    public static CharSequence formatCharSequence(CharSequence charSequence) {
        return formatCharSequence(charSequence, false);
    }

    private static final boolean ENABLE_HTML_FORMAT = false;

    public static CharSequence formatCharSequence(CharSequence charSequence, boolean autoLink) {
        CharSequence result = charSequence;
        if (ENABLE_HTML_FORMAT) {
            if (charSequence instanceof String) {
                if (!TextUtils.isEmpty(charSequence)) {
                    Spanned html = Html.fromHtml((String) charSequence);
                    SpannableStringBuilder builder = new SpannableStringBuilder(html);
                    removeSpan(builder, ImageSpan.class);
                    removeSpan(builder, URLSpan.class);
                    fixNewLines(builder);
                    if (autoLink) {
                        addLinks(builder);
                    }
                    result = new HtmlCharSequence((String) charSequence, builder);
                } else {
                    result = new SpannedString("");
                }
            } else if (charSequence instanceof SpannableStringBuilder) {
                if (!TextUtils.isEmpty(charSequence) && autoLink) {
                    addLinks((SpannableStringBuilder) charSequence);
                }
            }
        } else {
            if (charSequence instanceof String) {
                if (!TextUtils.isEmpty(charSequence)) {
                    SpannableStringBuilder builder = new SpannableStringBuilder(charSequence);
                    if (autoLink) {
                        addLinks(builder);
                    }
                    result = builder;
                } else {
                    result = new SpannedString("");
                }
            } else if (charSequence instanceof SpannableStringBuilder) {
                if (!TextUtils.isEmpty(charSequence) && autoLink) {
                    addLinks((SpannableStringBuilder) charSequence);
                }
            }
        }
        return result;
    }

    /**
     * Applies a regex to a Spannable turning the matches into links.
     *
     * @param s
     *            Spannable whose text is to be marked-up with links
     * @param p
     *            Regex pattern to be used for finding links
     * @param scheme
     *            Url scheme string (eg <code>http://</code> to be prepended to the url of links that do not have a
     *            scheme specified in the link text
     * @param matchFilter
     *            The filter that is used to allow the client code additional control over which pattern matches are to
     *            be converted into links.
     */
    private static final boolean addLinks(Spannable s) {
        boolean hasMatches = false;
        Matcher m = URL_PATTERN.matcher(s);

        while (m.find()) {
            int start = m.start(1);
            int end = m.end(1);
            String url = m.group(1);
            applyLink(url, start, end, s);
            hasMatches = true;
        }
        return hasMatches;
    }

    private static final void applyLink(String url, int start, int end, Spannable text) {
        URLSpan span = new URLSpan(url);

        text.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static void fixNewLines(SpannableStringBuilder builder) {
        int start = 0;
        int end = builder.length() - 1;
        while ((start <= end) && (builder.charAt(start) <= ' ')) {
            start++;
        }
        if (start > 0) {
            builder.delete(0, start);
        }
        start = 0;
        end = builder.length() - 1;
        while ((end >= start) && (builder.charAt(end) <= ' ')) {
            end--;
        }
        if (end < builder.length() - 1) {
            builder.delete(end, builder.length());
        }
        for (int i = 0; i < builder.length(); i++) {
            if (i + 2 < builder.length() && builder.charAt(i) == '\n' && builder.charAt(i + 1) == '\n'
                    && builder.charAt(i + 2) == '\n') {
                builder.delete(i, i + 1);
                i--;
            }
        }
    }

    private static void removeSpan(SpannableStringBuilder builder, Class type) {
        Object[] spans = builder.getSpans(0, builder.length(), type);
        if (spans != null) {
            for (int i = 0; i < spans.length; i++) {
                Object span = spans[i];
                int start = builder.getSpanStart(span);
                int end = builder.getSpanEnd(span);
                builder.delete(start, end);
            }
        }
    }

    /**
     * transform file bytes to string with unit
     *
     * @param bytes
     * @return
     */
    public static String formatFileSize(final Context context, final long bytes) {
        return Formatter.formatFileSize(context, bytes);
    }

    /**
     * transform file bytes to string with unit
     *
     * @param bytes
     * @return
     */
    public static String formatFileSize2(final long bytes) {
        float displayValue = bytes;
        String unit = BYTES_UNIT;
        String format = SIZE_FORMAT_WITHOUT_DIGIT;
        if (bytes > GB_UNIT_THREDHOLD) {
            displayValue /= ONE_GB;
            unit = GB_UNIT;
            format = SIZE_FORMAT;
        } else if (bytes > MB_UNIT_THREDHOLD) {
            displayValue /= ONE_MB;
            unit = MB_UNIT;
            format = SIZE_FORMAT;
        } else if (bytes > KB_UNIT_THREDHOLD) {
            // For KB size, we don't need to display digits.
            displayValue = (float)((double)bytes / ONE_KB);
            unit = KB_UNIT;
            format = SIZE_FORMAT_WITHOUT_DIGIT;
        }
        return String.format(format, displayValue, unit);
    }

    /**
     * orgainze application tags string to array
     *
     * @param tagsInfo
     * @return
     */
    public static String[] parseTags(final String tagsInfo) {
        String[] tags = new String[] {};
        if (!TextUtils.isEmpty(tagsInfo)) {
            tags = ArrayUtils.normalize(tagsInfo.split(","));
        }
        return tags;
    }

    /**
     * Check whether the email address is a valid email address.
     *
     * @param email
     *            the email address to check.
     * @return true if the email address is valid, false otherwise.
     */
    public static boolean isEmailValid(final String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        final Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    /**
     * Determine whether two objects are equal.
     *
     * @param <T>
     *            the type of the the objects.
     * @param one
     *            the first object.
     * @param other
     *            the second object.
     */
    public static <T> boolean equals(final T one, final T other) {
        if (one != null && other != null) {
            return one.equals(other);
        }
        return one == other;
    }

    /**
     * Determine whether two strings are equal.
     *
     * @param one
     *            the first object.
     * @param other
     *            the second object.
     */
    public static boolean equals(final String one, final String other) {
        return TextUtils.equals(one, other);
    }

    /**
     * Determine whether two strings are equal, ignoring case.
     *
     * @param one
     *            the first object.
     * @param other
     *            the second object.
     */
    public static boolean equalsIgnoreCase(final String one, final String other) {
        if (one != null && other != null) {
            return one.equalsIgnoreCase(other);
        }
        // Yes, we are checking reference equality here (null equality).
        // If both are null, true would be returned, otherwise it would be false.
        return equals((Object) one, (Object) other);
    }

    /**
     * Determine whether a nick name is valid.
     *
     * <p>
     * A valid nick name must match the following rule:
     * </p>
     * <ul>
     * <li>Have a length of 3-15.</li>
     * <li>Contains only underscore(_), letters or digits.</li>
     * <li>Digits can not be used as start of a nick name.</li>
     * <li>Must not contains any reserved words below:</li>
     * <ul>
     * <li>Tapler</li>
     * <li>Admin</li>
     * <li>System</li>
     * <li>MoboTap</li>
     * <li>MoboSquare</li>
     * </ul>
     * </ul>
     *
     * @param nicknName
     *            the nick name to check.
     * @return true if the nick name is valid, false otherwise.
     */
    public static boolean isNickNameValid(final String nicknName) {
        return NICK_NAME_PATTERN.matcher(nicknName).matches()
                && !NICK_NAME_RESERVED_PATTERN.matcher(nicknName).matches();
    }

    /**
     * Determine whether a comment is valid.
     *
     * <p>
     * A valid comment must match the following rule:
     * </p>
     * <ul>
     * <li>Have a length of 1-140.</li>
     * </ul>
     *
     * @param message
     *            the content of the comment.
     * @return true if the comment is valid, false otherwise.
     */
    public static boolean isCommentValid(final String message) {
        return !TextUtils.isEmpty(message) && message.length() <= MAX_COMMENT_LENGTH;
    }

    /**
     * Get UTF-8 bytes of a string.
     * @param s the string.
     * @return the UTF-8 bytes of the string.
     */
    public static byte[] getUtf8Bytes(String s) {
        try {
            return s.getBytes(ENCODING_UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is not supported.");
        }
    }

    /**
     * Get ASCII bytes of a string.
     * @param s the string.
     * @return the ASCII bytes of the string.
     */
    public static byte[] getAsciiBytes(String s) {
        try {
            return s.getBytes(ENCODING_ASCII);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("ASCII is not supported.");
        }
    }

    /**
     * Creates an string using UTF-8 encoding.
     * @return a string in UTF-8 encoding.
     */
    public static String newUtf8String(byte[] data, int offset, int length) {
        try {
            return new String(data, offset, length, ENCODING_UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is not supported.");
        }
    }

    /**
     * Creates an string using UTF-8 encoding.
     * @return a string in UTF-8 encoding.
     */
    public static String newUtf8String(byte[] data) {
        if (null == data) {
            throw new IllegalArgumentException("data may not be null.");
        }
        return newUtf8String(data, 0, data.length);
    }

    /**
     * Creates an string using ASCII encoding.
     * @param s the string.
     * @return the ASCII bytes of the string.
     */
    public static String newAsciiString(byte[] data, int offset, int length) {
        try {
            return new String(data, offset, length, ENCODING_ASCII);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("ASCII is not supported.");
        }
    }

    /**
     * Creates an string using ASCII encoding.
     * @return a string in ASCII encoding.
     */
    public static String newAsciiString(byte[] data) {
        if (null == data) {
            throw new IllegalArgumentException("data may not be null.");
        }
        return newAsciiString(data, 0, data.length);
    }

    /**
     * @param mAppName
     * @return
     */
    public static String valueOf(CharSequence value) {
        if (null == value) {
            return null;
        }
        return value.toString();
    }

    /**
     * @param firstPage
     * @return
     */
    public static String trimStart(CharSequence firstPage) {
        if (TextUtils.isEmpty(firstPage)) {
            return "";
        }
        String text = firstPage.toString();
        return trimStart(text);
    }

    private static final String THUMB_URL_TAG = "_is_thumbnail=true";

    public static String optThumbUrl(final String url) {
        return url + THUMB_URL_TAG;
    }

    public static String optUrl(final String thumbUrl) {
        return thumbUrl.replace(THUMB_URL_TAG, "");
    }

    public static boolean isLocalThumbUrl(final String thumbUrl) {
        return thumbUrl.contains(THUMB_URL_TAG);
    }

    public static String timeToString(long time) {

        return timeToString(time, Locale.getDefault());
    }

    public static String timeToString(long time, Locale locale) {

        final String dateString = DateFormat.getDateInstance(DateFormat.MEDIUM, locale)
                .format(time);
        final String timeString = DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(time);
        final String dateTimeString = String.format("%s, %s", dateString, timeString);
        return dateTimeString;
    }

    private static final String CHECK_CHINESE_REGULAR_EXPRESSION = "[\u4e00-\u9fa5]+";

    /**
     * A proxy class to access clipboard.
     *
     * @author chzhong
     *
     */
    public static abstract class ClipboardManagerProxy {

        /**
         * Retrieve a suitable {@linkplain ClipboardManagerProxy} to access clipboard.
         *
         * @param context
         *            the context of the application.
         * @return the suitable instance of {@linkplain ClipboardManagerProxy} to acces the clipboard.
         */
        public static final ClipboardManagerProxy getInstance(Context context) {
            if (null == context) {
                throw new IllegalArgumentException("context may not be not null.");
            }
            return getInstanceUnchecked(context);
        }

        static final ClipboardManagerProxy getInstanceUnchecked(Context context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                return new ClipboardManagerV1(context);
            } else {
                return new ClipboardManagerV11(context);
            }
        }

        /**
         * Retrieve the raw URI contained in this Item.
         */
        public abstract Uri getUri();

        /**
         * Returns the text on the clipboard.
         */
        public abstract CharSequence getText();

        /**
         * Returns true if the clipboard contains text or a text that can be convert into a URI; false otherwise.
         */
        public abstract boolean hasUri();

        /**
         * Returns true if the clipboard contains text; false otherwise.
         */
        public abstract boolean hasText();

        /**
         * Sets the contents of the clipboard to the specified URI (or its string representation).
         */
        public abstract void setUri(Uri uri);

        /**
         * Sets the contents of the clipboard to the specified text.
         */
        public abstract void setText(CharSequence text);

    }

    @SuppressWarnings("deprecation")
    static final class ClipboardManagerV1 extends ClipboardManagerProxy {

        private android.text.ClipboardManager mInstance;

        ClipboardManagerV1(Context context) {
            mInstance = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        }

        @Override
        public Uri getUri() {
            Uri result = null;
            try {
                CharSequence text = mInstance.getText();
                if (!TextUtils.isEmpty(text)) {
                    result = Uri.parse(text.toString());
                }
            } catch (Exception e) {
                // Not a URI.
            }
            return result;
        }

        @Override
        public CharSequence getText() {
            return mInstance.getText();
        }

        @Override
        public boolean hasUri() {
            Uri uri = getUri();
            return uri != null;
        }

        @Override
        public boolean hasText() {
            return mInstance.hasText();
        }

        @Override
        public void setText(CharSequence text) {
            mInstance.setText(text);
        }

        @Override
        public void setUri(Uri uri) {
            if (uri != null) {
                mInstance.setText(uri.toString());
            } else {
                mInstance.setText(null);
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    static final class ClipboardManagerV11 extends ClipboardManagerProxy {

        private ClipboardManager mInstance;

        ClipboardManagerV11(Context context) {
            mInstance = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        }

        @Override
        public Uri getUri() {
            ClipData clip = mInstance.getPrimaryClip();
            if (clip != null && clip.getItemCount() > 0) {
                return clip.getItemAt(0).getUri();
            }
            return null;
        }

        @Override
        public CharSequence getText() {
            ClipData clip = mInstance.getPrimaryClip();
            if (clip != null && clip.getItemCount() > 0) {
                return clip.getItemAt(0).getText();
            }
            return null;
        }

        @Override
        public boolean hasUri() {
            return getUri() != null;
        }

        @Override
        public boolean hasText() {
            return getText() != null;
        }

        @Override
        public void setUri(Uri uri) {
            if (uri != null) {
                ClipData clip = ClipData.newRawUri(uri.toString(), uri);
                mInstance.setPrimaryClip(clip);
            } else {
                setText(null);
            }
        }

        @Override
        public void setText(CharSequence text) {
            if (TextUtils.isEmpty(text)) {
                text = "";
            }
            ClipData clip = ClipData.newPlainText(text, text);
            mInstance.setPrimaryClip(clip);
        }

    }

    public static String neutralFormat(String format, Object... args) {
        return String.format(new Locale("", ""), format, args);
    }

    public static boolean urlStartsWithIgnoreScheme(String url1, String url2) {
        URI uri1 = null;
        URI uri2 = null;

        try {
            uri1 = URI.create(url1);
            uri2 = URI.create(url2);
        } catch (IllegalArgumentException e) {
            Log.e(e);
            return false;
        }

        if (uri1 == null || uri2 == null || uri1.getHost() == null || uri2.getHost() == null || uri1.getPath() == null
                || uri2.getPath() == null) {
            return false;
        }

        if ((uri1.getHost()).equalsIgnoreCase(uri2.getHost())) {
            if (uri1.getPath().toLowerCase(java.util.Locale.US).startsWith(uri2.getPath().toLowerCase(java.util.Locale.US))) {
                return true;
            }
        }

        return false;
    }

    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }

        return str.toLowerCase(java.util.Locale.US).startsWith(prefix.toLowerCase(java.util.Locale.US));
    }

    public static String getLikeUrl(String url) {
        String likeUrl = null;
        if (url.endsWith("/")) {
            likeUrl = url.substring(0, url.length() - 1);
        } else {
            likeUrl = url + "/";
        }
        return likeUrl;
    }

    public static boolean isContainChinese(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            if (str.substring(i, i + 1).matches(CHECK_CHINESE_REGULAR_EXPRESSION)) {
                return true;
            }
        }

        return false;
    }

    public static final CharSequence getClipboardText(Context context) {
        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }
        return ClipboardManagerProxy.getInstance(context).getText();

    }

    public static final Uri getClipboardUri(Context context) {
        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }
        return ClipboardManagerProxy.getInstance(context).getUri();

    }

    public static final void setClipboardText(Context context, CharSequence text) {

        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }

        ClipboardManagerProxy.getInstance(context).setText(text);
    }

    public static final void setClipboardUri(Context context, Uri uri) {

        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }

        ClipboardManagerProxy.getInstance(context).setUri(uri);
    }

    public static boolean hasUri(Context context) {

        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }

        return ClipboardManagerProxy.getInstance(context).hasUri();
    }

    public static boolean hasText(Context context) {

        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }

        return ClipboardManagerProxy.getInstance(context).hasText();
    }

    public static boolean isAllBlankCharacter(String str) {
        if (str == null) {
            return true;
        }
        if (str.trim().length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Get UTF-8 or default bytes of a string.
     * @param s the string.
     * @return the UTF-8 or default bytes of the string.
     */
    public static byte[] getUtf8OrDefaultBytes(String s) {
        if (null == s) {
            throw new IllegalArgumentException("s may not be null.");
        }
        try {
            return s.getBytes(ENCODING_UTF_8);
        } catch (UnsupportedEncodingException e) {
            return s.getBytes();
        }
    }

    /**
     * Creates an string using UTF-8 or default encoding.
     * @return a string in UTF-8 or default encoding.
     */
    public static String newUtf8OrDefaultString(byte[] data, int offset, int length) {
        if (null == data) {
            throw new IllegalArgumentException("data may not be null.");
        }
        try {
            return new String(data, offset, length, ENCODING_UTF_8);
        } catch (UnsupportedEncodingException e) {
            return new String(data, offset, length);
        }
    }

    /**
     * Creates an string using UTF-8 or default encoding.
     * @return a string in UTF-8 or default encoding.
     */
    public static String newUtf8OrDefaultString(byte[] data) {
        return newUtf8OrDefaultString(data, 0, data.length);
    }

    /**
     * @param str 12.4M
     */
    public static long formatFileSize(String str){

        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("str may not be null.");
        }

        String unit = getUnits(str);
        String num = getNums(str);
        double realNum = 0;
        double[] nums = {getNum(0),getNum(1), getNum(1),getNum(2),getNum(2),getNum(3), getNum(3),getNum(4)};
        String[] units = {"B", "KB", "K", "MB", "M", "GB", "G", "TB"};
        for(int k = 0;k < units.length;k++){
            if(unit.toUpperCase().equals(units[k])){
                double n = Double.parseDouble(num);
                realNum = n * nums[k];
                break;
            }
        }
        return (long)realNum;
    }

    private static String getNums(String str){
        String num = null;
        List<String> list = getList(str);
        int size = list.size();
        for(int i = 0;i < size;i++){
            String a = list.get(i);
            if(isNumber(a)){
                num = a;
            }
        }
        return num;
    }

    private static String getUnits(String str){
        String unit = null;
        List<String> list = getList(str);
        int size = list.size();
        for(int i = 0;i < size;i++){
            String a = list.get(i);
            if(isLetter(a)){
                unit = a;
            }
        }
        return unit;
    }

    private static double getNum(int n){
        return Math.pow(1024, n);
    }

    private static List<String> getList(String str){
        List<String> list = new ArrayList<String>();

        String s = "\\d+(.\\d)?|\\w+";
        Pattern pattern = Pattern.compile(s);
        Matcher ma = pattern.matcher(str);
        while(ma.find()){
            list.add(ma.group());
        }
        return list;
    }

    private static boolean isNumber(String str){
        return isTrue("\\d+",str);
    }

    private static boolean isLetter(String str){
        return isTrue("\\w+",str);
    }

    private static boolean isTrue(String reg,String str){
        Pattern pattern = Pattern.compile(reg);
        Matcher ma = pattern.matcher(str);
        while(ma.find()){
            return true;
        }
        return false;
    }

    public static JSONObject jsonFromString(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        try {
            return new JSONObject(text);
        } catch (Exception e) {
            Log.w(null, e);
        }
        return null;
    }

    public static String stringFromJson(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        return jsonObject.toString();
    }

    public static PointF pointFromString(String text) {
        String[] list = text.split(",");
        return new PointF(Float.parseFloat(list[0]), Float.parseFloat(list[1]));
    }

    public static String stringFromPoint(PointF point) {
        return point.x + "," + point.y;
    }

    public static List<String> arrayFromString(String text) {
        if (TextUtils.isEmpty(text)) {
            return new ArrayList<String>();
        }
        String[] list = text.split(",");
        ArrayList<String> arrayList = new ArrayList<String>(list.length);
        for (int i = 0; i < list.length; i++) {
            arrayList.add(list[i]);
        }
        return arrayList;
    }

    public static String stringFromArray(List<String> array) {
        return stringFromArray(array, ",");
    }

    public static String stringFromArray(List<String> array, String separator) {
        StringBuilder builder = new StringBuilder();
        if (array != null) {
            for (String string : array) {
                if (builder.length() > 0) {
                    builder.append(separator);
                }
                builder.append(string);
            }
        }
        return builder.toString();
    }

    public static String stringFromJsonArray(JSONArray array) throws Exception {
        StringBuilder builder = new StringBuilder();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                if (builder.length() > 0) {
                    builder.append(",");
                }
                builder.append(array.getString(i));
            }
        }
        return builder.toString();
    }

    public static List<String> arrayFromJsonArray(JSONArray array) throws Exception {
        List<String> list = new ArrayList<String>(array.length());
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getString(i));
        }
        return list;
    }

}
