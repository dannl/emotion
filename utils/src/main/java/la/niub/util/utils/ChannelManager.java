/*******************************************************************************
 *
 *    Copyright (c) Niub Info Tech Co. Ltd
 *
 *    NiubCoreLibrary
 *
 *    ChannelManager
 *    TODO File description or class description.
 *
 *    @author: dhu
 *    @since:  May 7, 2013
 *    @version: 1.0
 *
 ******************************************************************************/
package la.niub.util.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

/**
 * ChannelManager of NiubCoreLibrary.
 * @author dhu
 *
 */
public class ChannelManager {

    private static final String VERSION_KEY_PREFERENCES = "version_key_preferences";
    private static final String PREF_CHANNEL_NAME = "channel_name";
    private static final String CHANNEL_FILE = "channel.txt";
    public static final String DEFAULT_CHANNEL = "ofw";
    private static ChannelManager sInstance;
    private SharedPreferences mPreferences;
    private boolean mIsChannelLoaded;
    private String mChannel;
    private String mApkChannel;
    private ChannelManager(Context context) {
        mPreferences = context.getSharedPreferences(VERSION_KEY_PREFERENCES, Context.MODE_PRIVATE);
        mIsChannelLoaded = false;
    }

    public static synchronized ChannelManager getInstance() {
        if (null == sInstance) {
            sInstance = new ChannelManager(AppContext.getInstance());
        }
        return sInstance;
    }

    public synchronized String getChannel() {
        if (!mIsChannelLoaded) {
            loadChannel();
        }
        return mChannel;
    }

    public synchronized void updateChannel(String channel) {
        if (TextUtils.isEmpty(channel)) {
            return;
        }
        saveChannel(channel);
        mChannel = channel;
    }

    @SuppressLint("CommitPrefEdits")
    private synchronized void saveChannel(String channel) {
        PreferenceHelper.getInstance().save(
                mPreferences.edit().putString(PREF_CHANNEL_NAME, channel));
    }

    public synchronized String getApkChannel() {
        if (null == mApkChannel) {
            mApkChannel = IOUtilities.loadFromAssets(AppContext.getInstance(), CHANNEL_FILE);
            if (TextUtils.isEmpty(mApkChannel)) {
                mApkChannel = DEFAULT_CHANNEL;
            }
            mApkChannel = mApkChannel.trim();
        }
        return mApkChannel;
    }

    private void loadChannel() {
        String channel = null;
        boolean needSave = false;
        if (mPreferences.contains(PREF_CHANNEL_NAME)) {
            channel = mPreferences.getString(PREF_CHANNEL_NAME, DEFAULT_CHANNEL);
        } else {
            channel = getApkChannel();
            needSave = true;
        }
        if (TextUtils.isEmpty(channel)) {
            channel = DEFAULT_CHANNEL;
        }
        mChannel = channel.trim();
        Log.i("ChannelManager", "Channel is " + channel);
        mIsChannelLoaded = true;
        if (needSave) {
            saveChannel(channel);
        }
    }

    public boolean isDefaultChannel(){
        return TextUtils.equals(getChannel(),DEFAULT_CHANNEL);
    }

}
