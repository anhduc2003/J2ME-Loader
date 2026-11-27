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

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.playsoftware.j2meloader.R;
import ru.playsoftware.j2meloader.applist.AppItem;

public class AppsHorizontalAdapter extends RecyclerView.Adapter<AppsHorizontalAdapter.ViewHolder> {

	public interface OnAppClickListener {
		void onAppClick(AppItem item);
	}

	private final List<AppItem> apps;
	private final OnAppClickListener listener;

	public AppsHorizontalAdapter(List<AppItem> apps, OnAppClickListener listener) {
		this.apps = apps;
		this.listener = listener;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_library_app, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		AppItem app = apps.get(position);
		holder.txtLibraryTitle.setText(app.getTitle());
		Drawable icon = Drawable.createFromPath(app.getImagePathExt());
		holder.imgLibraryIcon.setImageDrawable(icon != null ? icon
				: holder.itemView.getContext().getDrawable(R.mipmap.ic_launcher));
		holder.itemView.setOnClickListener(v -> listener.onAppClick(app));
	}

	@Override
	public int getItemCount() {
		return apps.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		final ImageView imgLibraryIcon;
		final TextView txtLibraryTitle;

		ViewHolder(@NonNull View itemView) {
			super(itemView);
			imgLibraryIcon = itemView.findViewById(R.id.imgLibraryIcon);
			txtLibraryTitle = itemView.findViewById(R.id.txtLibraryTitle);
		}
	}
}
