package com.kydw.webviewdemo.adapter

import android.sax.TextElementListener
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kydw.webviewdemo.R

/**
 *@Author oyx
 *@date 2020/12/15 10:50
 *@description
 */
class SitesAdapter(sites: MutableList<String>, val listener: OnSiteClickListener) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_site, data = sites), DraggableModule {

    override fun convert(holder: BaseViewHolder, item: String) {
        if (data.size == 1) {
            holder.getView<ImageView>(R.id.img_add).visibility = View.VISIBLE
            holder.getView<ImageView>(R.id.img_del).visibility = View.INVISIBLE
        } else {
            if (holder.layoutPosition == data.size - 1) {
                holder.getView<ImageView>(R.id.img_add).visibility = View.VISIBLE
                holder.getView<ImageView>(R.id.img_del).visibility = View.VISIBLE
            } else {
                holder.getView<ImageView>(R.id.img_add).visibility = View.INVISIBLE
                holder.getView<ImageView>(R.id.img_del).visibility = View.INVISIBLE
            }
        }
        holder.getView<EditText>(R.id.et_sites).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    listener.afterTextChanged(s.toString(), holder.layoutPosition)
                }
            }
        })

        holder.getView<ImageView>(R.id.img_add).setOnClickListener {
            listener.onAdd(holder.layoutPosition)
        }
        holder.getView<ImageView>(R.id.img_del).setOnClickListener {
            listener.onDel(holder.layoutPosition)
        }
    }

    interface OnSiteClickListener {
        fun afterTextChanged(s: String, position: Int)
        fun onAdd(position: Int)
        fun onDel(position: Int)
    }


}