
package la.niub.util.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Set;
import java.util.Vector;

public class PrefsFile implements OnSharedPreferenceChangeListener {

    private final SharedPreferences mPreferences;
    private final Vector<OnSharedPreferenceChangeListener> mListeners =
            new Vector<OnSharedPreferenceChangeListener>();

    public PrefsFile() {
        this(null);
    }

    public PrefsFile(String fileName) {
        mPreferences = getPreferences(fileName, Context.MODE_PRIVATE);
        mPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            mListeners.add(listener);
        }
    }

    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            mListeners.remove(listener);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        for (OnSharedPreferenceChangeListener l : mListeners) {
            l.onSharedPreferenceChanged(sharedPreferences, key);
        }
    }

    public SharedPreferences getPreferences() {
        return mPreferences;
    }

    private SharedPreferences getPreferences(String name, int mode) {
        Context context = AppContext.getInstance();
        if (TextUtils.isEmpty(name)) {
            return PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            return context.getSharedPreferences(name, mode);
        }
    }

    public String getStringValue(String key, String defValue) {
        return mPreferences.getString(key, defValue);
    }

    public void setStringValue(String key, String value) {
        Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public Set<String> getStringSet(String key, Set<String> defValue) {
        return mPreferences.getStringSet(key, defValue);
    }

    public void setStringSet(String key, Set<String> value) {
        Editor editor = mPreferences.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public int getIntValue(String key, int defValue) {
        return mPreferences.getInt(key, defValue);
    }

    public void setIntValue(String key, int value) {
        Editor editor = mPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public long getLongValue(String key, long defValue) {
        return mPreferences.getLong(key, defValue);
    }

    public void setLongValue(String key, long value) {
        Editor editor = mPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public boolean getBooleanValue(String key, boolean defValue) {
        return mPreferences.getBoolean(key, defValue);
    }

    public void setBooleanValue(String key, boolean value) {
        Editor editor = mPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean contains(String key) {
        return mPreferences.contains(key);
    }

    public void clear() {
        mPreferences.edit().clear().apply();
    }

    public void remove(String key) {
        mPreferences.edit().remove(key).apply();
    }

}
