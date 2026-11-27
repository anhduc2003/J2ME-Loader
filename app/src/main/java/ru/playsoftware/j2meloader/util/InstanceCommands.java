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

package ru.playsoftware.j2meloader.util;

import android.content.Context;
import android.content.Intent;

/**
 * Commands sent from the multi-row dashboard to a running emulator process.
 */
public final class InstanceCommands {
	public static final String ACTION = "ru.playsoftware.j2meloader.INSTANCE_COMMAND";

	public static final String EXTRA_SLOT = "slot";
	public static final String EXTRA_COMMAND = "command";
	public static final String EXTRA_KEY_ACTION = "keyAction";
	public static final String EXTRA_KEY_CODE = "keyCode";
	public static final String EXTRA_PATH = "path";

	public static final String COMMAND_KEY = "key";
	public static final String COMMAND_CLOSE = "close";
	public static final String COMMAND_CAPTURE = "capture";

	private InstanceCommands() {
	}

	public static Intent command(Context context, int slot, String command) {
		return new Intent(ACTION)
				.setPackage(context.getPackageName())
				.putExtra(EXTRA_SLOT, slot)
				.putExtra(EXTRA_COMMAND, command);
	}

	public static void send(Context context, Intent intent) {
		context.sendBroadcast(intent);
	}
}
