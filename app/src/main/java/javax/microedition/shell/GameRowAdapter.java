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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.playsoftware.j2meloader.R;

public class GameRowAdapter extends RecyclerView.Adapter<GameRowAdapter.ViewHolder> {

	public interface OnRowActionListener {
		void onFullscreen(RunningRowInstance instance);

		void onSettings(RunningRowInstance instance);

		void onClose(int position);
	}

	private final Context context;
	private final List<RunningRowInstance> instances;
	private final OnRowActionListener listener;

	public GameRowAdapter(Context context, List<RunningRowInstance> instances,
						  OnRowActionListener listener) {
		this.context = context;
		this.instances = instances;
		this.listener = listener;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(R.layout.item_game_row, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		RunningRowInstance instance = instances.get(position);
		holder.txtGameTitle.setText(context.getString(R.string.multi_row_title_row,
				instance.getAppName(), String.valueOf(position + 1)));
		Drawable icon = Drawable.createFromPath(instance.getApp().getImagePathExt());
		holder.imgGameIcon.setImageDrawable(icon != null ? icon
				: context.getDrawable(R.mipmap.ic_launcher));

		holder.gameViewContainer.removeAllViews();
		View gameView = instance.getEmulatorView(context);
		holder.gameViewContainer.addView(gameView, new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		holder.bindKeys(instance);
		holder.btnZoom.setOnClickListener(v -> listener.onFullscreen(instance));
		holder.btnSettings.setOnClickListener(v -> listener.onSettings(instance));
		holder.btnClose.setOnClickListener(v -> {
			int index = holder.getBindingAdapterPosition();
			if (index != RecyclerView.NO_POSITION) {
				listener.onClose(index);
			}
		});
	}

	@Override
	public int getItemCount() {
		return instances.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		final TextView txtGameTitle;
		final ImageView imgGameIcon;
		final FrameLayout gameViewContainer;
		final ImageButton btnZoom;
		final ImageButton btnSettings;
		final ImageButton btnClose;
		final Button btnUp;
		final Button btnDown;
		final Button btnLeft;
		final Button btnRight;
		final Button btnOk;
		final Button btn1;
		final Button btn2;
		final Button btn3;
		final Button btn4;
		final Button btn5;
		final Button btn6;
		final Button btn7;
		final Button btn0;
		final Button btnStar;
		final Button btnHash;
		final Button btnClear;

		ViewHolder(@NonNull View itemView) {
			super(itemView);
			txtGameTitle = itemView.findViewById(R.id.txtGameTitle);
			imgGameIcon = itemView.findViewById(R.id.imgGameIcon);
			gameViewContainer = itemView.findViewById(R.id.gameViewContainer);
			btnZoom = itemView.findViewById(R.id.btnZoom);
			btnSettings = itemView.findViewById(R.id.btnSettings);
			btnClose = itemView.findViewById(R.id.btnClose);
			btnUp = itemView.findViewById(R.id.btnUp);
			btnDown = itemView.findViewById(R.id.btnDown);
			btnLeft = itemView.findViewById(R.id.btnLeft);
			btnRight = itemView.findViewById(R.id.btnRight);
			btnOk = itemView.findViewById(R.id.btnOk);
			btn1 = itemView.findViewById(R.id.btn1);
			btn2 = itemView.findViewById(R.id.btn2);
			btn3 = itemView.findViewById(R.id.btn3);
			btn4 = itemView.findViewById(R.id.btn4);
			btn5 = itemView.findViewById(R.id.btn5);
			btn6 = itemView.findViewById(R.id.btn6);
			btn7 = itemView.findViewById(R.id.btn7);
			btn0 = itemView.findViewById(R.id.btn0);
			btnStar = itemView.findViewById(R.id.btnStar);
			btnHash = itemView.findViewById(R.id.btnHash);
			btnClear = itemView.findViewById(R.id.btnClear);
		}

		void bindKeys(RunningRowInstance instance) {
			Context context = itemView.getContext();
			btnUp.setOnClickListener(v -> instance.sendKeyEvent(context, KeyEvent.KEYCODE_DPAD_UP));
			btnDown.setOnClickListener(v -> instance.sendKeyEvent(context, KeyEvent.KEYCODE_DPAD_DOWN));
			btnLeft.setOnClickListener(v -> instance.sendKeyEvent(context, KeyEvent.KEYCODE_DPAD_LEFT));
			btnRight.setOnClickListener(v -> instance.sendKeyEvent(context, KeyEvent.KEYCODE_DPAD_RIGHT));
			btnOk.setOnClickListener(v -> instance.sendKeyEvent(context, KeyEvent.KEYCODE_DPAD_CENTER));
			btn1.setOnClickListener(v -> instance.sendKeyEvent(context, KeyEvent.KEYCODE_1));
			btn2.setOnClickListener(v -> instance.sendKeyEvent(context, KeyEvent.KEYCODE_2));
			btn3.setOnClickListener(v -> instance.sendKeyEvent(context, KeyEvent.KEYCODE_3));
			btn4.setOnClickListener(v -> instance.sendKeyEvent(context, KeyEvent.KEYCODE_4));
			btn5.setOnClickListener(v -> instance.sendKeyEvent(context, KeyEvent.KEYCODE_5));
			btn6.setOnClickListener(v -> instance.sendKeyEvent(context, KeyEvent.KEYCODE_6));
			btn7.setOnClickListener(v -> instance.sendKeyEvent(context, KeyEvent.KEYCODE_7));
			btnStar.setOnClickListener(v -> instance.sendKeyEvent(context, KeyEvent.KEYCODE_STAR));
			btn0.setOnClickListener(v -> instance.sendKeyEvent(context, KeyEvent.KEYCODE_0));
			btnHash.setOnClickListener(v -> instance.sendKeyEvent(context, KeyEvent.KEYCODE_POUND));
			btnClear.setOnClickListener(v -> instance.sendKeyEvent(context, KeyEvent.KEYCODE_DEL));
		}
	}
}
