package net.monoflop.filedirectorypicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder>
{
	enum ViewMode
	{
		Default, //Files and folders are shown and selectable
		FilesOnly, //Files and folders are shown but only files are selectable
		FoldersOnly //Only folders are shown and selectable
	}

	private Context context;
	private List<FileDirectoryPickerDialog.Entry> entryList;
	private EntrySelectedCallback entrySelectedCallback;
	private ViewMode viewMode;

	EntryAdapter(@NonNull Context context,
	             @NonNull List<FileDirectoryPickerDialog.Entry> entryList,
	             @NonNull EntrySelectedCallback entrySelectedCallback,
	             @Nullable ViewMode viewMode)
	{
		this.context = context;
		this.entryList = entryList;
		this.entrySelectedCallback = entrySelectedCallback;

		this.viewMode = viewMode;
		if(this.viewMode == null)this.viewMode = ViewMode.Default;
	}

	@Override
	@NonNull
	public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		RelativeLayout layout;
		layout = (RelativeLayout) LayoutInflater.from(parent.getContext())
				.inflate(R.layout.element_entry, parent, false);

		return new EntryViewHolder(layout);
	}

	@Override
	public void onBindViewHolder(@NonNull EntryViewHolder holder, int position)
	{
		FileDirectoryPickerDialog.Entry entry = entryList.get(position);

		holder.entryCheckBox.setOnCheckedChangeListener(null);
		holder.entryCheckBox.setChecked(entry.isSelected());

		if(entry.getEntryType() == FileDirectoryPickerDialog.EntryType.Folder)
		{
			holder.entryImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_folder_black_24dp));
			if(viewMode == ViewMode.Default || viewMode == ViewMode.FoldersOnly)
				holder.entryCheckBox.setVisibility(View.VISIBLE);
			else
				holder.entryCheckBox.setVisibility(View.GONE);
		}
		else if(entry.getEntryType() == FileDirectoryPickerDialog.EntryType.File)
		{
			holder.entryImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_insert_drive_file_black_24dp));

			if(viewMode == ViewMode.Default || viewMode == ViewMode.FilesOnly)
				holder.entryCheckBox.setVisibility(View.VISIBLE);
			else
				holder.entryCheckBox.setVisibility(View.GONE);
		}
		else
		{
			holder.entryImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
			holder.entryCheckBox.setVisibility(View.GONE);
		}

		holder.entryName.setText(entry.getName());
		holder.entryInfo.setText(entry.getInfo());

		holder.root.setOnClickListener((v) ->
		{
			if(entry.getEntryType() == FileDirectoryPickerDialog.EntryType.File)
			{
				if(viewMode == ViewMode.Default || viewMode == ViewMode.FilesOnly)
				{
					holder.entryCheckBox.setChecked(!holder.entryCheckBox.isChecked());
				}
			}
			else
			{
				entrySelectedCallback.onFolderClicked(entry);
			}
		});

		holder.entryCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
				entrySelectedCallback.onEntrySelected(entry, isChecked));
	}

	@Override
	public int getItemCount()
	{
		return entryList.size();
	}

	static class EntryViewHolder extends RecyclerView.ViewHolder
	{
		@BindView(R.id.entryImage) ImageView entryImage;
		@BindView(R.id.entryName) TextView entryName;
		@BindView(R.id.entryInfo) TextView entryInfo;
		@BindView(R.id.entryCheckBox) CheckBox entryCheckBox;

		RelativeLayout root;

		private EntryViewHolder(RelativeLayout view)
		{
			super(view);
			root = view;
			ButterKnife.bind(this, view);
		}
	}

	public interface EntrySelectedCallback
	{
		void onEntrySelected(FileDirectoryPickerDialog.Entry entry, boolean selected);
		void onFolderClicked(FileDirectoryPickerDialog.Entry entry);
	}
}
