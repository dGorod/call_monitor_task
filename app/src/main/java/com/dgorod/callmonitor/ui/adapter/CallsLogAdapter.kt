package com.dgorod.callmonitor.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dgorod.callmonitor.databinding.ListitemCallBinding
import com.dgorod.callmonitor.ui.model.CallUiModel

/**
 * [RecyclerView] adapter to hold calls log.
 */
class CallsLogAdapter: ListAdapter<CallUiModel, CallLogViewHolder>(CallUiDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CallLogViewHolder(ListitemCallBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: CallLogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object CallUiDiffCallback: DiffUtil.ItemCallback<CallUiModel>() {
        override fun areItemsTheSame(oldItem: CallUiModel, newItem: CallUiModel): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: CallUiModel, newItem: CallUiModel): Boolean {
            return oldItem == newItem
        }
    }
}

class CallLogViewHolder(
    private val binding: ListitemCallBinding
): RecyclerView.ViewHolder(binding.root) {

    /**
     * Binds call data to list item.
     *
     * @param model with data.
     */
    fun bind(model: CallUiModel) {
        with(binding) {
            nameLabel.text = model.name
            durationLabel.text = model.duration
        }
    }
}
