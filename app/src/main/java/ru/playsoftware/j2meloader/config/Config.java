/*
 * Copyright 2018-2019 Nikita Shakarun
 * Copyright 2020-2023 Yury Kharchenko
 * Copyright 2022 Arman Jussupgaliyev
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

package ru.playsoftware.j2meloader.config;

import static ru.playsoftware.j2meloader.util.Constants.ACTION_EDIT;
import static ru.playsoftware.j2meloader.util.Constants.KEY_MIDLET_NAME;
import static ru.playsoftware.j2meloader.util.Constants.PREF_EMULATOR_DIR;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.Keep;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.IOException;

import javax.microedition.shell.MicroActivity;
import javax.microedition.shell.MicroActivityInstance2;
import javax.microedition.shell.MicroActivityInstance3;
import javax.microedition.util.ContextHolder;

import ru.playsoftware.j2meloader.BuildConfig;
import ru.playsoftware.j2meloader.R;

import static ru.playsoftware.j2meloader.util.Constants.*;

import ru.playsoftware.j2meloader.util.FileUtils;

public class Config {
	public static final String DEX_OPT_CACHE_DIR = "dex_opt";
	public static final String FS_DIR = "/fs/";
	public static final String MIDLET_CONFIG_FILE = "/config.json";
	public static final String MIDLET_CONFIGS_DIR = "/configs/";
	public static final String MIDLET_DATA_DIR = "/data/";
	public static final String MIDLET_DEX_FILE = "/converted.dex";
	public static final String MIDLET_ICON_FILE = "/icon.png";
	public static final String MIDLET_KEY_LAYOUT_FILE = "/VirtualKeyboardLayout";
	public static final String MIDLET_MANIFEST_FILE = MIDLET_DEX_FILE + ".conf";
	public static final String MIDLET_RES_DIR = "/res";
	public static final String MIDLET_RES_FILE = "/res.jar";
	public static final String SCREENSHOTS_DIR;
	public static final String SHADERS_DIR = "/shaders/";
	public static final String SOUNDBANKS_DIR = "/soundbanks/";
	public static final String APPS_DB_NAME = "/J2ME-apps.db";
	public static final String INSTANCES_DIR = "/instances/";
	public static final int MAX_INSTANCES = 3;

	private static String emulatorDir;
	private static String dataDir;
	private static String configsDir;
	private static String profilesDir;
	private static String appDir;
	private static int instanceSlot = 1;

	@Keep
	private static final SharedPreferences.OnSharedPreferenceChangeListener sPrefListener =
			(sharedPreferences, key) -> {
				if (PREF_EMULATOR_DIR.equals(key)) {
					initDirs(sharedPreferences.getString(key, emulatorDir));
				}
			};

	static {
		Context context = ContextHolder.getAppContext();
		String appName = "J2ME-Loader";
		if (!BuildConfig.FULL_EMULATOR) {
			appName = context.getString(R.string.app_name);
		}
		SCREENSHOTS_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
				+ "/" + appName;
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String path = FileUtils.isExternalStorageLegacy() ?
				preferences.getString(PREF_EMULATOR_DIR, null) :
				context.getExternalFilesDir(null).getPath();
		if (path == null) {
			path = Environment.getExternalStorageDirectory() + "/" + appName;
		}
		initDirs(path);
		preferences.registerOnSharedPreferenceChangeListener(sPrefListener);
	}

	public static int getInstanceSlot() {
		return instanceSlot;
	}

	public static void setInstanceSlot(int slot) {
		instanceSlot = Math.max(1, slot);
	}

	public static String getInstanceDataDir() {
		return instanceSlot > 1 ? dataDir + INSTANCES_DIR + instanceSlot : dataDir;
	}

	public static File getSlotDataDir(int slot) {
		return new File(dataDir + INSTANCES_DIR + slot);
	}

	public static void prepareInstanceData(String appPath) {
		if (instanceSlot <= 1) {
			return;
		}
		String appName = new File(appPath).getName();
		File target = new File(getInstanceDataDir(), appName);
		if (target.exists()) {
			return;
		}
		copyTree(new File(dataDir, appName), target);
		File instanceFs = new File(emulatorDir + FS_DIR + "instance-" + instanceSlot);
		if (!instanceFs.exists()) {
			copyTree(new File(emulatorDir + FS_DIR + "c"), new File(instanceFs, "c"));
			instanceFs.mkdirs();
		}
	}

	private static void copyTree(File src, File dst) {
		if (src.isDirectory()) {
			File[] files = src.listFiles();
			if (files == null) {
				return;
			}
			if (!dst.isDirectory() && !dst.mkdirs()) {
				return;
			}
			for (File file : files) {
				copyTree(file, new File(dst, file.getName()));
			}
		} else if (src.isFile()) {
			File parent = dst.getParentFile();
			if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
				return;
			}
			try {
				FileUtils.copyFileUsingChannel(src, dst);
			} catch (IOException ignored) {
			}
		}
	}

	public static int findFreeMidletSlot(Context context) {
		ActivityManager activityManager =
				(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		boolean second = false;
		boolean third = false;
		if (activityManager != null) {
			for (ActivityManager.AppTask task : activityManager.getAppTasks()) {
				Intent intent = task.getTaskInfo().baseIntent;
				ComponentName component = intent == null ? null : intent.getComponent();
				String className = component == null ? null : component.getClassName();
				if (MicroActivityInstance2.class.getName().equals(className)) {
					second = true;
				} else if (MicroActivityInstance3.class.getName().equals(className)) {
					third = true;
				}
			}
		}
		if (!second) {
			return 2;
		}
		return third ? -1 : 3;
	}

	public static void startAppInSlot(Context context, String name, String path, int slot) {
		Class<? extends Activity> activity = slot >= 3
				? MicroActivityInstance3.class : MicroActivityInstance2.class;
		Intent intent = new Intent(Intent.ACTION_DEFAULT, Uri.parse(path), context, activity);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(KEY_MIDLET_NAME, name);
		context.startActivity(intent);
	}

	public static String getEmulatorDir() {
		return emulatorDir;
	}

	public static String getDataDir() {
		return dataDir;
	}

	public static String getConfigsDir() {
		return configsDir;
	}

	public static String getProfilesDir() {
		return profilesDir;
	}

	public static String getAppDir() {
		return appDir;
	}

	public static String getShadersDir() {
		return emulatorDir + SHADERS_DIR;
	}

	public static String getSoundbanksDir() {
		return emulatorDir + SOUNDBANKS_DIR;
	}

	public static String getFsInternalDir() {
		return emulatorDir + FS_DIR + (instanceSlot > 1 ? "instance-" + instanceSlot + "/" : "") + "c/";
	}

	public static String getFsExternalDir() {
		if (FileUtils.isExternalStorageLegacy()) {
			return Environment.getExternalStorageDirectory().getPath() + "/";
		} else {
			return emulatorDir + FS_DIR + "e/";
		}
	}

	public static void startApp(Context context, String name, String path, boolean showSettings) {
		startApp(context, name, path, showSettings, null);
	}

	public static void startApp(Context context, String name, String path, boolean showSettings, String arguments) {
		File appDir = new File(path);
		String workDir = appDir.getParentFile().getParent();
		File file = new File(workDir + Config.MIDLET_CONFIGS_DIR + appDir.getName());
		if (showSettings || !file.exists()) {
			Intent intent = new Intent(ACTION_EDIT, Uri.parse(path),
					context, ConfigActivity.class);
			intent.putExtra(KEY_MIDLET_NAME, name);
			intent.putExtra(KEY_START_ARGUMENTS, arguments);
			context.startActivity(intent);
		} else {
			Intent intent = new Intent(Intent.ACTION_DEFAULT, Uri.parse(path),
					context, MicroActivity.class);
			intent.putExtra(KEY_MIDLET_NAME, name);
			intent.putExtra(KEY_START_ARGUMENTS, arguments);
			context.startActivity(intent);
		}
	}

	private static void initDirs(String path) {
		emulatorDir = path;
		dataDir = emulatorDir + MIDLET_DATA_DIR;
		configsDir = emulatorDir + MIDLET_CONFIGS_DIR;
		profilesDir = emulatorDir + "/templates/";
		appDir = emulatorDir + "/converted/";
	}
}
