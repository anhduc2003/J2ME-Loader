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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import ru.playsoftware.j2meloader.applist.AppItem;

/**
 * Filtering rules for the application library: a text query and an optional folder.
 */
public final class LibraryFilter {
	private LibraryFilter() {
	}

	public static boolean matchesQuery(AppItem item, CharSequence constraint) {
		if (constraint == null) {
			return true;
		}
		String query = constraint.toString().trim().toLowerCase(Locale.ROOT);
		if (query.isEmpty()) {
			return true;
		}
		return contains(item.getTitle(), query) || contains(item.getAuthor(), query);
	}

	public static boolean matchesFolder(AppItem item, String folder) {
		if (folder == null) {
			return true;
		}
		String itemFolder = item.getFolder();
		if (itemFolder == null || itemFolder.trim().isEmpty()) {
			return false;
		}
		return itemFolder.equals(folder);
	}

	public static boolean matches(AppItem item, CharSequence constraint, String folder) {
		return matchesQuery(item, constraint) && matchesFolder(item, folder);
	}

	public static List<String> collectFolders(Iterable<AppItem> items) {
		TreeSet<String> folders = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (AppItem item : items) {
			String folder = item.getFolder();
			if (folder != null && !folder.trim().isEmpty()) {
				folders.add(folder);
			}
		}
		return new ArrayList<>(folders);
	}

	private static boolean contains(String value, String query) {
		return value != null && value.toLowerCase(Locale.ROOT).contains(query);
	}
}
