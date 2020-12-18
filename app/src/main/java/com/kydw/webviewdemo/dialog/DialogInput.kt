package com.kydw.webviewdemo.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kydw.webviewdemo.R
import com.kydw.webviewdemo.adapter.SitesAdapter
import com.kydw.webviewdemo.util.ToastUtil
import kotlinx.android.synthetic.main.dialog_input.*
import kotlinx.android.synthetic.main.dialog_input.view.*

/**
 *@Author oyx
 *@date 2020/12/14 10:33
 *@description
 */
class DialogInput : DialogFragment(), SitesAdapter.OnSiteClickListener {
    val sites = mutableListOf<String>("")
    private val siteAdapter: SitesAdapter = SitesAdapter(sites, this)
    private lateinit var listener: OnConfirmClickListener
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = inflater.inflate(R.layout.dialog_input, container, false)
        view.apply {
            view.but_cancel.setOnClickListener { dialog!!.cancel() }
            view.but_confirm.setOnClickListener {
                val kw = view?.et_kw?.text.toString()
                if (kw != null && kw.isNotEmpty() || (sites.size == 1 && sites[0].isEmpty())) {
                    listener.onConfirm(et_kw.text.toString(), sites)
                    dialog!!.cancel()
                } else {
                    ToastUtil.show(context, "请输入关键词和网址")
                }

            }
        }

        view.recy_sites.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = siteAdapter

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
        val wm=context!!.getSystemService(Context.WINDOW_SERVICE)
        val w=(wm as WindowManager).defaultDisplay.width
        val h=(wm as WindowManager).defaultDisplay.height
        val scale: Float = context!!.resources!!.displayMetrics!!.density
        dialog!!.window!!.setLayout(w, 550 * scale.toInt())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnConfirmClickListener
    }

    interface OnConfirmClickListener {
        fun onConfirm(kw: String, sites: MutableList<String>)
    }

    override fun afterTextChanged(s: String, position: Int) {
        sites[position] = s
    }

    override fun onAdd(position: Int) {
        sites.add("")
        siteAdapter.notifyDataSetChanged()

    }

    override fun onDel(position: Int) {
        sites.removeAt(position)
        siteAdapter.notifyDataSetChanged()
    }

}