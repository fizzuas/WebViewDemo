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
import com.kydw.webviewdemo.util.ToastUtil
import kotlinx.android.synthetic.main.dialog_add_site.view.*

/**
 *@Author oyx
 *@date 2020/12/30 17:17
 *@description
 */
const val DIALOG_INPUT_SITE = "DIALOG_INPUT_SITE"

class DialogInputSite : DialogFragment() {
    private lateinit var listener: OnOKListener
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = inflater.inflate(R.layout.dialog_add_site, container, false)
        view.apply {
            view.but_ok.setOnClickListener {
                if (et_site_input.text.isEmpty()) {
                    ToastUtil.show(activity, "请输入网址")
                } else {
                    listener.onAddSiteOk(et_site_input.text.toString())
                    view.et_site_input.setText("")
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
        listener = context as OnOKListener
    }

    interface OnOKListener {
        fun onAddSiteOk(site: String)
    }

}