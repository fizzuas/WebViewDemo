package com.kydw.webviewdemo.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kydw.webviewdemo.R

/**
 *@Author oyx
 *@date 2020/12/12 17:03
 *@description
 */
class ModelAdapter(data: MutableList<Model>) :
    BaseQuickAdapter<Model, BaseViewHolder>(R.layout.item_model, data = data), DraggableModule {
    override fun convert(holder: BaseViewHolder, item: Model) {
        holder.setText(R.id.tv_kw, item.keyword)
        holder.setText(R.id.tv_sites, item.sites)

    }
}