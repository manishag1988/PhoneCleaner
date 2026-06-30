package com.phonecleaner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phonecleaner.R
import com.phonecleaner.databinding.ItemMediaBinding
import com.phonecleaner.model.MediaFile

class MediaAdapter(
    private val files: List<MediaFile>,
    private val onDeleteClick: (MediaFile) -> Unit,
    private val onSelectionChanged: (Boolean) -> Unit
) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    private val selectedItems = mutableSetOf<Long>()
    var isSelectionMode = false
        private set

    fun toggleSelectionMode() {
        isSelectionMode = !isSelectionMode
        if (!isSelectionMode) {
            selectedItems.clear()
        }
        notifyDataSetChanged()
    }

    fun selectAll() {
        selectedItems.clear()
        files.forEach { selectedItems.add(it.id) }
        notifyDataSetChanged()
        onSelectionChanged(selectedItems.isNotEmpty())
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
        onSelectionChanged(false)
    }

    fun getSelectedFiles(): List<MediaFile> {
        return files.filter { selectedItems.contains(it.id) }
    }

    fun hasSelection(): Boolean = selectedItems.isNotEmpty()

    inner class MediaViewHolder(private val binding: ItemMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(file: MediaFile) {
            binding.textViewName.text = file.displayName
            binding.textViewSize.text = file.getSizeString()
            binding.textViewType.text = file.mimeType

            val icon = when {
                file.isGif -> R.drawable.ic_gif
                file.mimeType.startsWith("video") -> R.drawable.ic_video
                else -> R.drawable.ic_image
            }
            binding.imageViewIcon.setImageResource(icon)

            if (isSelectionMode) {
                binding.checkBoxSelect.visibility = View.VISIBLE
                binding.checkBoxSelect.isChecked = selectedItems.contains(file.id)
                binding.checkBoxSelect.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedItems.add(file.id)
                    } else {
                        selectedItems.remove(file.id)
                    }
                    onSelectionChanged(selectedItems.isNotEmpty())
                }
            } else {
                binding.checkBoxSelect.visibility = View.GONE
                binding.checkBoxSelect.setOnCheckedChangeListener(null)
            }

            binding.buttonDelete.setOnClickListener {
                onDeleteClick(file)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemMediaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(files[position])
    }

    override fun getItemCount(): Int = files.size
}
