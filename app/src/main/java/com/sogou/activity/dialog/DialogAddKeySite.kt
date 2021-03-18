package com.sogou.activity.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sogou.activity.R
import com.sogou.activity.adapter.KeysAdapter
import com.sogou.activity.adapter.SitesAdapter
import com.sogou.activity.util.ToastUtil
import kotlinx.android.synthetic.main.dialog_add_key_site.view.*


class DialogAddKeySite : DialogFragment(), SitesAdapter.OnSiteClickListener,
    KeysAdapter.OnKeyClickListener {
    private val sites = mutableListOf<String>("")
    private val keys = mutableListOf<String>("")
    private val keysAdapter: KeysAdapter = KeysAdapter(keys, this)
    private val siteAdapter: SitesAdapter = SitesAdapter(sites, this)
    private lateinit var listener: OnConfirmClickListener
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = inflater.inflate(R.layout.dialog_add_key_site, container, false)
        view.apply {
            view.but_cancel.setOnClickListener { dialog!!.cancel() }
            view.but_confirm.setOnClickListener {
                if (keys.isNotEmpty() &&sites.isNotEmpty()) {
                    listener.onAddKeySiteConfirm(keys, sites)
                    dialog!!.cancel()
                } else {
                    ToastUtil.show(context, "请输入关键词和网址")
                }
            }
        }

        view.recy_kws.apply {
            layoutManager=LinearLayoutManager(context)
            adapter=keysAdapter
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
        val wm = context!!.getSystemService(Context.WINDOW_SERVICE)
        val w = (wm as WindowManager).defaultDisplay.width
        val h = (wm as WindowManager).defaultDisplay.height
        val scale: Float = context!!.resources!!.displayMetrics!!.density
        dialog!!.window!!.setLayout(w, 550 * scale.toInt())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnConfirmClickListener
    }

    interface OnConfirmClickListener {
        fun onAddKeySiteConfirm(kw: MutableList<String>, sites: MutableList<String>)
    }

    override fun afterSiteChanged(s: String, position: Int) {
        sites[position] = s
    }

    override fun onAddSite(position: Int) {
        sites.add("")
        siteAdapter.notifyDataSetChanged()
    }

    override fun onDelSite(position: Int) {
        sites.removeAt(position)
        siteAdapter.notifyDataSetChanged()

    }

    override fun afterKeyChanged(s: String, position: Int) {
        keys[position]=s
    }

    override fun onAddKey(position: Int) {
        keys.add("")
        keysAdapter.notifyDataSetChanged()
    }

    override fun onDelKey(position: Int) {
        keys.removeAt(position)
        keysAdapter.notifyDataSetChanged()
    }

}