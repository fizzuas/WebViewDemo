package com.kydw.webviewdemo.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.kydw.webviewdemo.R
import com.kydw.webviewdemo.adapter.KEY
import com.kydw.webviewdemo.adapter.POSITION
import com.kydw.webviewdemo.adapter.VALUE_SITE
import com.kydw.webviewdemo.util.ToastUtil
import kotlinx.android.synthetic.main.dialog_input_kw.view.*


/**
 *@Author oyx
 *@date 2021/1/13 15:59
 *@description
 */
class DialogEditKW() : DialogFragment() {
    private lateinit var listener: OnKW2Listener
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val position = arguments!!.getInt(POSITION)
        val key=arguments!!.getString(KEY)
        val valueSite=arguments!!.getString(VALUE_SITE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = inflater.inflate(R.layout.dialog_input_kw, container, false)
        view.apply {
            if(key!=null){
                view.et_kw_input.setText(key)
            }
            if(valueSite!=null){
                view.et_site_input.setText(valueSite)
            }
            view.but_ok.setOnClickListener {
                if (et_site_input.text.isEmpty() || et_kw_input.text.isEmpty()) {
                    ToastUtil.show(activity, "请输入关键词和网址")
                } else {
                    listener.onEditKWOK(position,
                        et_kw_input.text.toString(),
                        et_site_input.text.toString())
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
        dialog!!.window!!.setLayout(350 * scale.toInt(), 180 * scale.toInt())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnKW2Listener
    }

    interface OnKW2Listener {
        fun onEditKWOK(position: Int, kw: String, site: String)
    }

}