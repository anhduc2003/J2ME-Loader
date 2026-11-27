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

import org.junit.Test;

import ru.playsoftware.j2meloader.applist.AppItem;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LibraryFilterTest {

	private static AppItem item(String title, String author, String folder) {
		AppItem item = new AppItem("path", title, author, "1.0");
		item.setFolder(folder);
		return item;
	}

	@Test
	public void matchesWholeLibraryWhenQueryIsBlank() {
		AppItem item = item("Worms", "EA", null);
		assertTrue(LibraryFilter.matchesQuery(item, null));
		assertTrue(LibraryFilter.matchesQuery(item, ""));
		assertTrue(LibraryFilter.matchesQuery(item, "   "));
	}

	@Test
	public void matchesTitleIgnoringCaseAndPadding() {
		AppItem item = item("Bounce", "Nokia", null);
		assertTrue(LibraryFilter.matchesQuery(item, "bounce"));
		assertTrue(LibraryFilter.matchesQuery(item, "  BOU "));
		assertFalse(LibraryFilter.matchesQuery(item, "snake"));
	}

	@Test
	public void matchesAuthor() {
		AppItem item = item("Bounce", "Nokia", null);
		assertTrue(LibraryFilter.matchesQuery(item, "nokia"));
	}

	@Test
	public void matchesEveryFolderWhenFolderIsNull() {
		AppItem unfiled = item("Bounce", "Nokia", null);
		AppItem filed = item("Worms", "EA", "Strategy");
		assertTrue(LibraryFilter.matchesFolder(unfiled, null));
		assertTrue(LibraryFilter.matchesFolder(filed, null));
	}

	@Test
	public void matchesOnlyItemsOfSelectedFolder() {
		AppItem unfiled = item("Bounce", "Nokia", null);
		AppItem blank = item("Snake", "Nokia", "  ");
		AppItem action = item("Asphalt", "Gameloft", "Action");
		assertTrue(LibraryFilter.matchesFolder(action, "Action"));
		assertFalse(LibraryFilter.matchesFolder(unfiled, "Action"));
		assertFalse(LibraryFilter.matchesFolder(blank, "Action"));
		assertFalse(LibraryFilter.matchesFolder(action, "Strategy"));
	}

	@Test
	public void collectsFoldersOnceIgnoringCaseAndBlankValues() {
		List<AppItem> items = Arrays.asList(
				item("Asphalt", "Gameloft", "Action"),
				item("Bounce", "Nokia", null),
				item("Snake", "Nokia", "  "),
				item("Worms", "EA", "Strategy"),
				item("Rayman", "Ubisoft", "action"));
		assertEquals(Arrays.asList("Action", "Strategy"), LibraryFilter.collectFolders(items));
	}

	@Test
	public void collectsNoFoldersFromUnfiledLibrary() {
		List<AppItem> items = Collections.singletonList(item("Bounce", "Nokia", null));
		assertTrue(LibraryFilter.collectFolders(items).isEmpty());
	}

	@Test
	public void combinesQueryAndFolder() {
		AppItem action = item("Asphalt", "Gameloft", "Action");
		AppItem strategy = item("Asphalt 2", "Gameloft", "Strategy");
		assertTrue(LibraryFilter.matches(action, "asphalt", "Action"));
		assertFalse(LibraryFilter.matches(strategy, "asphalt", "Action"));
		assertTrue(LibraryFilter.matches(strategy, "asphalt", null));
	}
}
