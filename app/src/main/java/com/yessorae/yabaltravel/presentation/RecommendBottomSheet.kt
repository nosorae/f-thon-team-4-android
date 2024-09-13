package com.yessorae.yabaltravel.presentation

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yessorae.yabaltravel.common.AdapterRecommend
import com.yessorae.yabaltravel.common.BottomSheetListener
import com.yessorae.yabaltravel.common.Define
import com.yessorae.yabaltravel.databinding.BottomRecommendBinding
import com.yessorae.yabaltravel.presentation.model.RecommendBottomData
import com.yessorae.yabaltravel.presentation.model.RecommendItem
import com.yessorae.yabaltravel.presentation.model.Recommendation

class RecommendBottomSheet(private val recommendList: ArrayList<RecommendItem> , private val viewContext : Context) :
    BottomSheetDialogFragment() {
    private var listener: BottomSheetListener? = null
    private lateinit var binding: BottomRecommendBinding
    private val adapter by lazy {
        RecommendAdapter()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        addListener()
    }

    private fun init() {
        binding.recyclerviewRecommend.layoutManager = LinearLayoutManager(viewContext)
        adapter.recommendList = recommendList
        binding.recyclerviewRecommend.adapter = adapter
    }

    private fun addListener() {
//        binding.btnLetGo.setOnClickListener {
//            listener?.onBottomSheetDismissed(Define.BOTTOM_SHEET_SELECT)
//            dismiss()
//        }
        adapter.setOnClickListener(object : AdapterRecommend{
            override fun setOnClickListener(data: RecommendItem) {
                Log.d(this.javaClass.name , "User Select Data")
                listener?.onBottomSheetDismissed(RecommendBottomData(Define.BOTTOM_SHEET_SELECT , data))
                dismiss()
            }
        })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onBottomSheetDismissed(RecommendBottomData(Define.BOTTOM_SHEET_NO, null))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val d = dialogInterface as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = resources.displayMetrics.heightPixels // Set the height to the screen height
                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            dismiss()
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        // No-op
                    }
                })
            }
        }
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomSheetListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement BottomSheetListener")
        }
    }
}