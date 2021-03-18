package com.sogou.activity.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.sogou.activity.R
import com.sogou.activity.adapter.POSITION
import com.sogou.activity.adapter.VALUE_SITE
import com.sogou.activity.util.ToastUtil
import kotlinx.android.synthetic.main.dialog_add_key_site.view.but_cancel
import kotlinx.android.synthetic.main.dialog_add_site.view.*

/**
 *@Author oyx
 *@date 2021/1/13 16:33
 *@description
 */

class DialogEditSite : DialogFragment(){
    private lateinit var listener: OnSite2Listener
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val position = arguments!!.getInt(POSITION)
        val site = arguments!!.getString(VALUE_SITE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = inflater.inflate(R.layout.dialog_add_site, container, false)
        view.apply {
            if(site!=null){
                et_site_input.setText(site)
            }
            view.but_ok.setOnClickListener {
                if (et_site_input.text.isEmpty()) {
                    ToastUtil.show(activity, "请输入网址")
                } else {
                    listener.onEditSiteOK(position,et_site_input.text.toString())
                    dismiss()
                }
            }
            but_cancel.setOnClickListener {
                dismiss()
            }
        }
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        isCancelable = false
        return dialog
    }

    override fun onStart() {
        super.onStart()

        val scale: Float = context!!.resources!!.displayMetrics!!.density
        dialog!!.window!!.setLayout(280 * scale.toInt(), 126 * scale.toInt())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnSite2Listener
    }

    interface OnSite2Listener {
        fun onEditSiteOK(position:Int, site: String)
    }
}