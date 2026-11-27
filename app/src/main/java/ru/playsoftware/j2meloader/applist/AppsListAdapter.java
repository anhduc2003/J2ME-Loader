/*
 * Copyright 2015-2016 Nickolay Savchenko
 * Copyright 2017-2019 Nikita Shakarun
 * Copyright 2019-2023 Yury Kharchenko
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

package ru.playsoftware.j2meloader.applist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ru.playsoftware.j2meloader.R;
import ru.playsoftware.j2meloader.databinding.ListRowGridJarBinding;
import ru.playsoftware.j2meloader.databinding.ListRowJarBinding;
import ru.playsoftware.j2meloader.util.LibraryFilter;

class AppsListAdapter extends ListAdapter<AppItem, AppsListAdapter.AppViewHolder> implements Filterable {
	static final int LAYOUT_TYPE_LIST = 0;
	static final int LAYOUT_TYPE_GRID = 1;

	interface OnAppClickListener {
		void onAppClick(AppItem item);
	}

	interface OnSelectionChangeListener {
		void onSelectionChanged(int count);
	}

	private final View.OnCreateContextMenuListener contextMenuListener;
	private final OnAppClickListener appClickListener;
	private List<AppItem> list = new ArrayList<>();
	private final AppFilter appFilter = new AppFilter();
	private final Set<Integer> selectedIds = new LinkedHashSet<>();
	private CharSequence filterConstraint;
	private String folderConstraint;
	private View emptyView;
	private OnSelectionChangeListener selectionListener;
	private boolean selectionMode;
	private int selectionColor;
	private int layout = LAYOUT_TYPE_LIST;

	AppsListAdapter(View.OnCreateContextMenuListener contextMenuListener, OnAppClickListener appClickListener) {
		super(new DiffUtil.ItemCallback<AppItem>() {
			@Override
			public boolean areItemsTheSame(@NonNull AppItem oldItem, @NonNull AppItem newItem) {
				return oldItem.getId() == newItem.getId();
			}

			@Override
			public boolean areContentsTheSame(@NonNull AppItem oldItem, @NonNull AppItem newItem) {
				return oldItem.getTitle().equals(newItem.getTitle()) &&
						oldItem.getVersion().equals(newItem.getVersion()) &&
						oldItem.getLastPlayed() == newItem.getLastPlayed() &&
						Objects.equals(oldItem.getFolder(), newItem.getFolder());
			}
		});
		this.contextMenuListener = contextMenuListener;
		this.appClickListener = appClickListener;
	}

	@Override
	public int getItemViewType(int position) {
		return layout;
	}

	@NonNull
	@Override
	public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		if (viewType == 1) {
			ListRowGridJarBinding binding = ListRowGridJarBinding.inflate(inflater, parent, false);
			return new AppViewHolder(binding, contextMenuListener);
		} else {
			ListRowJarBinding binding = ListRowJarBinding.inflate(inflater, parent, false);
			return new AppListViewHolder(binding, contextMenuListener);
		}
	}

	@Override
	public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
		holder.onBind(getItem(position));
	}

	void setItems(List<AppItem> items) {
		list = items;
		appFilter.filter(filterConstraint);
	}

	void setFolderFilter(String folder) {
		if (Objects.equals(folder, folderConstraint)) {
			return;
		}
		folderConstraint = folder;
		appFilter.filter(filterConstraint);
	}

	List<String> getFolders() {
		return LibraryFilter.collectFolders(list);
	}

	void setSelectionListener(OnSelectionChangeListener listener) {
		selectionListener = listener;
	}

	boolean isSelectionMode() {
		return selectionMode;
	}

	void setSelectionMode(boolean enabled) {
		if (selectionMode == enabled) {
			return;
		}
		selectionMode = enabled;
		selectedIds.clear();
		notifyDataSetChanged();
	}

	boolean isSelected(AppItem item) {
		return selectedIds.contains(item.getId());
	}

	void toggleSelection(AppItem item) {
		int position = getCurrentList().indexOf(item);
		if (position < 0) {
			return;
		}
		if (!selectedIds.remove(item.getId())) {
			selectedIds.add(item.getId());
		}
		notifyItemChanged(position);
		if (selectionListener != null) {
			selectionListener.onSelectionChanged(selectedIds.size());
		}
	}

	void selectAll() {
		selectedIds.clear();
		for (AppItem item : getCurrentList()) {
			selectedIds.add(item.getId());
		}
		notifyDataSetChanged();
		if (selectionListener != null) {
			selectionListener.onSelectionChanged(selectedIds.size());
		}
	}

	List<AppItem> getSelectedItems() {
		List<AppItem> selected = new ArrayList<>();
		for (AppItem item : getCurrentList()) {
			if (selectedIds.contains(item.getId())) {
				selected.add(item);
			}
		}
		return selected;
	}

	private int getSelectionColor(Context context) {
		if (selectionColor == 0) {
			TypedValue value = new TypedValue();
			if (context.getTheme().resolveAttribute(android.R.attr.colorControlHighlight, value, true)) {
				selectionColor = value.data;
			} else {
				selectionColor = Color.TRANSPARENT;
			}
		}
		return selectionColor;
	}

	@Override
	public Filter getFilter() {
		return appFilter;
	}

	void setEmptyView(View emptyView) {
		this.emptyView = emptyView;
	}

	void setLayout(int layout) {
		this.layout = layout;
	}

	static class AppViewHolder extends RecyclerView.ViewHolder {
		private final ImageView icon;
		private final TextView name;
		private final Drawable defaultBackground;

		AppViewHolder(ListRowGridJarBinding binding, View.OnCreateContextMenuListener contextMenuListener) {
			this(binding.getRoot(), binding.listTitle, binding.listImage, contextMenuListener);
		}

		AppViewHolder(View itemView,
					  TextView listTitle,
					  ImageView listImage,
					  View.OnCreateContextMenuListener contextMenuListener) {
			super(itemView);
			icon = listImage;
			name = listTitle;
			defaultBackground = itemView.getBackground();

			itemView.setOnClickListener(v -> {
				AppsListAdapter adapter = (AppsListAdapter) getBindingAdapter();
				if (adapter == null) {
					return;
				}
				AppItem item = adapter.getItem(getLayoutPosition());
				if (adapter.selectionMode) {
					adapter.toggleSelection(item);
				} else {
					adapter.appClickListener.onAppClick(item);
				}
			});
			itemView.setOnCreateContextMenuListener(contextMenuListener);
		}

		void onBind(AppItem item) {
			Drawable icon = Drawable.createFromPath(item.getImagePathExt());
			if (icon != null) {
				icon.setFilterBitmap(false);
				this.icon.setImageDrawable(icon);
			} else {
				this.icon.setImageResource(R.mipmap.ic_launcher);
			}
			name.setText(item.getTitle());
			itemView.setTag(item);
			AppsListAdapter adapter = (AppsListAdapter) getBindingAdapter();
			if (adapter != null && adapter.isSelectionMode() && adapter.isSelected(item)) {
				itemView.setBackgroundColor(adapter.getSelectionColor(itemView.getContext()));
			} else {
				itemView.setBackground(defaultBackground);
			}
		}
	}

	static class AppListViewHolder extends AppViewHolder {
		private final TextView author;
		private final TextView version;

		AppListViewHolder(ListRowJarBinding binding, View.OnCreateContextMenuListener contextMenuListener) {
			super(binding.getRoot(), binding.listTitle, binding.listImage, contextMenuListener);
			author = binding.listAuthor;
			version = binding.listVersion;
		}

		@Override
		void onBind(AppItem item) {
			super.onBind(item);
			author.setText(item.getAuthor());
			version.setText(item.getVersion());
		}
	}

	class AppFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			String folder = folderConstraint;
			if (isEmpty(constraint) && folder == null) {
				results.count = list.size();
				results.values = list;
			} else {
				ArrayList<AppItem> resultList = new ArrayList<>();
				for (AppItem item : list) {
					if (LibraryFilter.matches(item, constraint, folder)) {
						resultList.add(item);
					}
				}
				results.count = resultList.size();
				results.values = resultList;
			}
			return results;
		}

		boolean isEmpty(CharSequence constraint) {
			return constraint == null || constraint.toString().trim().length() == 0;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			filterConstraint = constraint;
			//noinspection unchecked
			submitList((List<AppItem>) results.values);
			if (results.count > 0) {
				emptyView.setVisibility(View.GONE);
			} else {
				int message;
				if (list.isEmpty()) {
					message = R.string.no_data_for_display;
				} else if (isEmpty(constraint)) {
					message = R.string.msg_folder_empty;
				} else {
					message = R.string.msg_no_matches;
				}
				String msg = message == R.string.msg_no_matches
						? emptyView.getResources().getString(message, constraint)
						: emptyView.getResources().getString(message);
				((TextView) emptyView).setText(msg);
				emptyView.setVisibility(View.VISIBLE);
			}
		}
	}
}
