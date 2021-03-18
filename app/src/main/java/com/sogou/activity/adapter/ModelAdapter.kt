package com.sogou.activity.adapter

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sogou.activity.R
import com.sogou.activity.dialog.DialogEditKW
import com.sogou.activity.dialog.DialogEditSite

const val DIALOG_INPUT_SITE_ADAPTER = "DIALOG_INPUT_SITE_ADAPTER"
const val DIALOG_INPUT_KW_ADAPTER = "DIALOG_INPUT_KW_ADAPTER"
const val POSITION = "POSITION"
const val KEY = "KEY"
const val VALUE_SITE = "VALUE_SITE"

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
                val dialogSiteEdit: DialogEditKW = DialogEditKW()
                val bundle = Bundle()
                bundle.putInt(POSITION, holder.layoutPosition)
                bundle.putString(KEY,item.keyword)
                bundle.putString(VALUE_SITE,item.site)
                dialogSiteEdit.arguments = bundle
                dialogSiteEdit.show((context as FragmentActivity).supportFragmentManager,
                    DIALOG_INPUT_SITE_ADAPTER)
            } else {
                val dialogKWEdit: DialogEditSite = DialogEditSite()
                val bundle = Bundle()
                bundle.putInt(POSITION, holder.layoutPosition)
                bundle.putString(VALUE_SITE,item.keyword)
                dialogKWEdit.arguments = bundle
                dialogKWEdit.show((context as FragmentActivity).supportFragmentManager,
                    DIALOG_INPUT_KW_ADAPTER)
            }

        }

    }
}