
package la.niub.util.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static boolean equals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }

    public static byte[] getBytes(String text) {
        return getBytes(text, "UTF-8");
    }

    public static byte[] getBytes(String text, String charsetName) {
        if (text == null) {
            return null;
        }
        try {
            return text.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(Object object) {
        if (object == null) {
            return null;
        }
        return object.toString();
    }

    /**
     * Check whether the email is effective
     * 
     * @param email
     * @return true
     */
    public static boolean isEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        Pattern emailPattern = Pattern
                .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = emailPattern.matcher(email);
        return m.matches();
    }

    public static String formatPhoneNumber(String number) {
        if (TextUtils.isEmpty(number)) {
            return "";
        }
        if (number != null && number.startsWith("+86")) {
            number = number.substring(3);
        }
        number = number.replace("-", "").replace(" ", "");
        return number;
    }

    public static String mergeStringArray(String[] array, String divider) {
        StringBuilder builder = new StringBuilder();
        for (String text : array) {
            builder.append(text).append(divider);
        }
        return builder.toString();
    }

    // string regexstr = @"<[^>]*>"; //去除所有的标签
    // @"<script[^>]*?>.*?</script>" //去除所有脚本，中间部分也删除
    // string regexstr = @"<img[^>]*>"; //去除图片的正则
    // string regexstr = @"<(?!br).*?>"; //去除所有标签，只剩br
    // string regexstr = @"<table[^>]*?>.*?</table>"; //去除table里面的所有内容
    // string regexstr = @"<(?!img|br|p|/p).*?>"; //去除所有标签，只剩img,br,p
    // 去掉所有html标签
    public static String fromHtml(String html) {
        if (!TextUtils.isEmpty(html)) {
            return html.replaceAll("<[^>]*>", "");
        }
        return html;
    }

    public static String joinString(String[] arrays, String split) {
        StringBuilder build = new StringBuilder();
        if (null != arrays) {
            for (int i = 0; i < arrays.length; i++) {
                build.append(arrays[i]);
                build.append(split);
            }
        }
        if (build.length() > 0) {
            // 去掉最后一个split;
            return build.substring(0, build.length() - split.length());
        }
        return build.toString();
    }

    // 以空白符分隔字符串
    public static String[] splitWhitespace(String text) {
        return text.split("\\s{1,}");
    }

    // 将首字母转为大写
    public static String toUpperCaseFirstLetter(String text) {
        if (!TextUtils.isEmpty(text)) {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }
        return text;
    }

    // 判断一个字符串是否全为小写
    public static boolean isLowerCase(String text) {
        char[] array = text.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (!Character.isLowerCase(array[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean containChinese(String text) {
        boolean hasChinese = false;
        if (!TextUtils.isEmpty(text)) {
            Pattern chinesePattern = Pattern.compile("[\u4e00-\u9fa5]");
            Matcher matcher = chinesePattern.matcher(text);
            if (matcher.find()) {
                hasChinese = true;
            }
        }
        return hasChinese;
    }

    /**
     * Html-encode the string.
     * 
     * @param s the string to be encoded
     * @return the encoded string
     */
    public static String htmldecode(String t) {
        String text = t;
        text = text.replaceAll("&amp;", "&");
        text = text.replaceAll("&lt;", "<");
        text = text.replaceAll("&gt;", ">");
        text = text.replaceAll("&quot;", "\"");
        text = text.replaceAll("&apos;", "\'");
        return text;
    }

    public static String optString(JSONObject object, String key) {
        final String value = object.optString(key);
        return !JSONObject.NULL.equals(value) ? value.toString() : null;
    }

    public static JSONObject asJsonObject(String text) {
        if (!TextUtils.isEmpty(text)) {
            try {
                return new JSONObject(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static JSONArray asJSONArray(String text) {
        if (!TextUtils.isEmpty(text)) {
            try {
                return new JSONArray(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String grep(String text, String grep) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }
        StringBuilder builder = new StringBuilder();
        String[] array = text.split("\n");

        for (String str : array) {
            if (str.contains(grep)) {
                builder.append(str).append("\n");
            }
        }
        return builder.toString();
    }

}
