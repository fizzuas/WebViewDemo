package com.kydw.webviewdemo.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kydw.webviewdemo.R

class SitesAdapter(sites: MutableList<String>, val listener: OnSiteClickListener) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_site, data = sites), DraggableModule {

    @SuppressLint("ClickableViewAccessibility")
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

        holder.getView<EditText>(R.id.et_site).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                    listener.afterSiteChanged(s.toString(), holder.layoutPosition)
            }
        })

        holder.getView<ImageView>(R.id.img_add).setOnClickListener {
            listener.onAddSite(holder.layoutPosition)
        }
        holder.getView<ImageView>(R.id.img_del).setOnClickListener {
            listener.onDelSite(holder.layoutPosition)
        }

        val et = holder.getView<EditText>(R.id.et_site)
        et.setText(item)
        if (holder.layoutPosition == data.size - 1) {
            et.isFocusableInTouchMode = true
            et.requestFocusFromTouch()
        } else {
            et.isFocusableInTouchMode = false
        }

        et.setOnTouchListener { v, event ->
            et.isFocusableInTouchMode = true
            false
        }
    }

    interface OnSiteClickListener {
        fun afterSiteChanged(s: String, position: Int)
        fun onAddSite(position: Int)
        fun onDelSite(position: Int)
    }


}