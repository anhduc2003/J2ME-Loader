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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.playsoftware.j2meloader.R;
import ru.playsoftware.j2meloader.applist.AppItem;
import ru.playsoftware.j2meloader.config.Config;
import ru.playsoftware.j2meloader.settings.SettingsActivity;
import ru.playsoftware.j2meloader.util.AppUtils;

public class MultiRowActivity extends AppCompatActivity {
	private static final int MAX_EXTRA_ROWS = 2;
	private static final long THUMBNAIL_INTERVAL = 900;

	private final List<RunningRowInstance> rows = new ArrayList<>();
	private final Handler handler = new Handler(Looper.getMainLooper());

	private GameRowAdapter rowAdapter;
	private CheckBox enableRowMode;
	private Spinner rowLimit;
	private File thumbnailDir;

	private final Runnable thumbnailTick = new Runnable() {
		@Override
		public void run() {
			for (int i = 0; i < rows.size(); i++) {
				RunningRowInstance instance = rows.get(i);
				if (!instance.isRunning(MultiRowActivity.this)) {
					continue;
				}
				instance.requestThumbnail(MultiRowActivity.this);
				if (instance.loadThumbnail()) {
					rowAdapter.notifyItemChanged(i);
				}
			}
			handler.postDelayed(this, THUMBNAIL_INTERVAL);
		}
	};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_row);

		thumbnailDir = new File(Config.getDataDir(), "thumbnails");
		if (!thumbnailDir.isDirectory() && !thumbnailDir.mkdirs()) {
			thumbnailDir = getCacheDir();
		}

		findViewById(R.id.btnGlobalSettings).setOnClickListener(v ->
				startActivity(new Intent(this, SettingsActivity.class)));

		RecyclerView library = findViewById(R.id.rvHorizontalApps);
		library.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		List<AppItem> apps = AppUtils.loadInstalledApps();
		if (apps.isEmpty()) {
			Toast.makeText(this, R.string.multi_row_no_games, Toast.LENGTH_SHORT).show();
		}
		library.setAdapter(new AppsHorizontalAdapter(apps, this::onLibraryAppClick));

		enableRowMode = findViewById(R.id.cbEnableFixedRow);
		rowLimit = findViewById(R.id.spinnerRows);
		List<String> limits = new ArrayList<>();
		for (int i = 1; i <= MAX_EXTRA_ROWS; i++) {
			limits.add(Integer.toString(i));
		}
		rowLimit.setAdapter(new ArrayAdapter<>(this,
				android.R.layout.simple_spinner_dropdown_item, limits));

		RecyclerView active = findViewById(R.id.rvActiveRows);
		active.setLayoutManager(new LinearLayoutManager(this));
		rowAdapter = new GameRowAdapter(this, rows, new GameRowAdapter.OnRowActionListener() {
			@Override
			public void onFullscreen(RunningRowInstance instance) {
				instance.bringToFront(MultiRowActivity.this);
			}

			@Override
			public void onSettings(RunningRowInstance instance) {
				Config.startApp(MultiRowActivity.this, instance.getAppName(),
						instance.getAppPath(), true);
			}

			@Override
			public void onClose(int position) {
				RunningRowInstance instance = rows.remove(position);
				instance.close(MultiRowActivity.this);
				rowAdapter.notifyItemRemoved(position);
			}
		});
		active.setAdapter(rowAdapter);
	}

	private void onLibraryAppClick(AppItem app) {
		if (!enableRowMode.isChecked()) {
			Config.startApp(this, app.getTitle(), app.getPathExt(), false);
			return;
		}
		int limit = rowLimit.getSelectedItemPosition() + 1;
		if (rows.size() >= limit) {
			Toast.makeText(this, getString(R.string.multi_row_limited, limit),
					Toast.LENGTH_SHORT).show();
			return;
		}
		int slot = Config.findFreeMidletSlot(this);
		if (slot < 0) {
			Toast.makeText(this, getString(R.string.multi_row_limited, MAX_EXTRA_ROWS),
					Toast.LENGTH_SHORT).show();
			return;
		}
		RunningRowInstance instance = new RunningRowInstance(app, slot, thumbnailDir);
		rows.add(instance);
		rowAdapter.notifyItemInserted(rows.size() - 1);
		Config.startAppInSlot(this, app.getTitle(), app.getPathExt(), slot);
	}

	@Override
	protected void onResume() {
		super.onResume();
		handler.removeCallbacks(thumbnailTick);
		handler.post(thumbnailTick);
	}

	@Override
	protected void onPause() {
		super.onPause();
		handler.removeCallbacks(thumbnailTick);
	}
}
