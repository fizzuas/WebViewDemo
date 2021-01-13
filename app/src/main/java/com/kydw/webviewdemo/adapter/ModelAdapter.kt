package com.kydw.webviewdemo.adapter

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kydw.webviewdemo.R
import com.kydw.webviewdemo.dialog.Dialog2KW
import com.kydw.webviewdemo.dialog.Dialog2Site

const val DIALOG_INPUT_SITE_ADAPTER = "DIALOG_INPUT_SITE_ADAPTER"
const val DIALOG_INPUT_KW_ADAPTER = "DIALOG_INPUT_KW_ADAPTER"
const val POSITION = "POSITION"

class ModelAdapter(data: MutableList<Model>) :
    BaseQuickAdapter<Model, BaseViewHolder>(R.layout.item_model, data = data), DraggableModule {

    override fun convert(holder: BaseViewHolder, item: Model) {
        holder.setText(R.id.tv_kw, item.keyword)
        holder.setText(R.id.tv_sites, item.site)

        holder.getView<TextView>(R.id.right_menu_del).setOnClickListener {
            data.removeAt(holder.layoutPosition)
            notifyDataSetChanged()
        }
        holder.getView<TextView>(R.id.right_menu_edit).setOnClickListener {
            Log.e("oyx", "type=" + data[holder.layoutPosition].type.toString());
            if (data[holder.layoutPosition].type == KEYWORD_SITE) {
                val dialogSite2: Dialog2KW = Dialog2KW()
                val bundle = Bundle()
                bundle.putInt(POSITION, holder.layoutPosition)
                dialogSite2.arguments = bundle
                dialogSite2.show((context as FragmentActivity).supportFragmentManager,
                    DIALOG_INPUT_SITE_ADAPTER)
            } else {
                val dialogKW2: Dialog2Site = Dialog2Site()
                val bundle = Bundle()
                bundle.putInt(POSITION, holder.layoutPosition)
                dialogKW2.arguments = bundle
                dialogKW2.show((context as FragmentActivity).supportFragmentManager,
                    DIALOG_INPUT_KW_ADAPTER)
            }

        }

    }
}