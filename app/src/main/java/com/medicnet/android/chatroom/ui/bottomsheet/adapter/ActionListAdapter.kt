package com.medicnet.android.chatroom.ui.bottomsheet.adapter

import android.view.MenuItem
import com.medicnet.android.R
import com.medicnet.android.util.extensions.setVisible

/**
 * An adapter for bottomsheet menu that lists all the actions that could be taken over a chat message.
 */
class ActionListAdapter(
    menuItems: List<MenuItem> = emptyList(),
    callback: MenuItem.OnMenuItemClickListener
) : ListBottomSheetAdapter(menuItems = menuItems, callback = callback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = menuItems[position]

        if (showIcons) {
            holder.imageIcon.setVisible(item.icon != null)
        } else {
            holder.imageIcon.setVisible(false)
        }

        holder.imageIcon.setImageDrawable(item.icon)
        holder.textTitle.text = item.title
        holder.itemView.setOnClickListener {
            callback?.onMenuItemClick(item)
        }
        val deleteTextColor = holder.itemView.context.resources.getColor(R.color.red)
        val color = if (item.itemId == R.id.action_message_delete) {
            deleteTextColor
        } else {
            textColors.get(item.itemId)
        }
        holder.textTitle.setTextColor(color)
    }
}