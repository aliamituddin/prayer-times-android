/*
 * Copyright (c) 2013-2017 Metin Kale
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.metinkale.prayerapp.vakit.sounds;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;

import com.crashlytics.android.Crashlytics;
import com.metinkale.prayerapp.App;
import com.metinkale.prayerapp.utils.MD5;
import com.metinkale.prayerapp.vakit.alarm.Alarm;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class AppSound extends Sound {


    @Getter @Setter(AccessLevel.PACKAGE)
    private String name;
    @Getter @Setter(AccessLevel.PACKAGE)
    private String shortName;
    @Getter
    private final String md5;
    @Getter
    private final int size;
    private final String url;

    private transient boolean checkedMD5;

    AppSound(String name, int size, String md5, String url) {
        this.name = name;
        this.size = size;
        this.md5 = md5;
        this.url = url;
    }

    public int getId() {
        return url.hashCode();
    }

    @Override
    public Set<AppSound> getAppSounds() {
        return Collections.singleton(this);
    }

    public File getFile() {
        String path = url.substring(url.indexOf("/sounds/") + 8);

        File file = new File(App.get().getExternalFilesDir(
                Environment.DIRECTORY_MUSIC), path);
        file.getParentFile().mkdirs();
        return file;
    }


    public Uri getUri() {
        return Uri.fromFile(getFile());
    }

    private void checkMD5() {
        File file = getFile();
        if (file.exists()) {
            if (size != file.length() || (md5 != null && !MD5.checkMD5(md5, file))) {
                file.delete();
            }
        }
    }

    @Override
    public MediaPlayer createMediaPlayer(Alarm alarm) {
        if (!isDownloaded()) return null;
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(App.get(), getUri());
        } catch (IOException e) {
            Crashlytics.logException(e);
            return null;
        }
        return mp;
    }

    public boolean isDownloaded() {
        if (!checkedMD5) {
            checkMD5();
        }
        checkedMD5 = getFile().exists();
        return checkedMD5;
    }


    public String getUrl() {
        return App.API_URL + url;
    }


}
