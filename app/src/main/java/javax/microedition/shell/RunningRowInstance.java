/*
 * Copyright 2026 Nikita Shakarun
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

package javax.microedition.shell;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import ru.playsoftware.j2meloader.applist.AppItem;
import ru.playsoftware.j2meloader.util.InstanceCommands;

public class RunningRowInstance {
	private static final long KEY_RELEASE_DELAY = 90;

	private final AppItem app;
	private final int slot;
	private final File thumbnailFile;
	private final Handler handler = new Handler(Looper.getMainLooper());

	private Bitmap thumbnail;
	private long thumbnailStamp;

	public RunningRowInstance(AppItem app, int slot, File thumbnailDir) {
		this.app = app;
		this.slot = slot;
		this.thumbnailFile = new File(thumbnailDir, "row-" + slot + ".png");
	}

	public String getAppName() {
		return app.getTitle();
	}

	public String getAppPath() {
		return app.getPathExt();
	}

	public AppItem getApp() {
		return app;
	}

	public int getSlot() {
		return slot;
	}

	public View getEmulatorView(Context context) {
		ImageView view = new ImageView(context);
		view.setScaleType(ImageView.ScaleType.FIT_CENTER);
		view.setImageBitmap(thumbnail);
		return view;
	}

	public void requestThumbnail(Context context) {
		Intent intent = InstanceCommands.command(context, slot, InstanceCommands.COMMAND_CAPTURE);
		intent.putExtra(InstanceCommands.EXTRA_PATH, thumbnailFile.getAbsolutePath());
		InstanceCommands.send(context, intent);
	}

	public boolean loadThumbnail() {
		if (!thumbnailFile.isFile()) {
			return false;
		}
		long stamp = thumbnailFile.lastModified();
		if (stamp == thumbnailStamp) {
			return false;
		}
		Bitmap bitmap = BitmapFactory.decodeFile(thumbnailFile.getAbsolutePath());
		if (bitmap == null) {
			return false;
		}
		thumbnailStamp = stamp;
		thumbnail = bitmap;
		return true;
	}

	public void sendKeyEvent(Context context, int keyCode) {
		Intent down = InstanceCommands.command(context, slot, InstanceCommands.COMMAND_KEY);
		down.putExtra(InstanceCommands.EXTRA_KEY_CODE, keyCode);
		down.putExtra(InstanceCommands.EXTRA_KEY_ACTION, KeyEvent.ACTION_DOWN);
		InstanceCommands.send(context, down);
		handler.postDelayed(() -> {
			Intent up = InstanceCommands.command(context, slot, InstanceCommands.COMMAND_KEY);
			up.putExtra(InstanceCommands.EXTRA_KEY_CODE, keyCode);
			up.putExtra(InstanceCommands.EXTRA_KEY_ACTION, KeyEvent.ACTION_UP);
			InstanceCommands.send(context, up);
		}, KEY_RELEASE_DELAY);
	}

	public void close(Context context) {
		InstanceCommands.send(context, InstanceCommands.command(context, slot, InstanceCommands.COMMAND_CLOSE));
		thumbnailFile.delete();
	}

	public boolean isRunning(Context context) {
		return findTask(context) != null;
	}

	public void bringToFront(Context context) {
		ActivityManager.AppTask task = findTask(context);
		if (task == null) {
			return;
		}
		try {
			int id = task.getTaskInfo().id;
			ActivityManager activityManager =
					(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			if (activityManager != null) {
				activityManager.moveTaskToFront(id, ActivityManager.MOVE_TASK_WITH_HOME);
			}
		} catch (RuntimeException ignored) {
			task.moveToFront();
		}
	}

	private ActivityManager.AppTask findTask(Context context) {
		ActivityManager activityManager =
				(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (activityManager == null) {
			return null;
		}
		String target = slotActivity(slot).getName();
		for (ActivityManager.AppTask task : activityManager.getAppTasks()) {
			Intent base = task.getTaskInfo().baseIntent;
			ComponentName component = base == null ? null : base.getComponent();
			if (component != null && target.equals(component.getClassName())) {
				return task;
			}
		}
		return null;
	}

	private Class<? extends MicroActivity> slotActivity(int slot) {
		return slot >= 3 ? MicroActivityInstance3.class : MicroActivityInstance2.class;
	}
}
