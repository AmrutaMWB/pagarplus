package com.pagarplus.app.modules.itemlistdialog.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import retrofit2.HttpException
import java.lang.Exception
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Unit
import android.app.Activity
import android.widget.EditText
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseDialogFragment
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.databinding.DialogItemlistdialogBinding
import com.pagarplus.app.extensions.IntentParameters
import com.pagarplus.app.extensions.NoInternetConnection
import com.pagarplus.app.extensions.isJSONObject
import com.pagarplus.app.extensions.showProgressDialog
import com.pagarplus.app.modules.itemlistdialog.data.model.ItemlistModel
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import com.pagarplus.app.modules.itemlistdialog.data.viewmodel.ItemlistVM
import com.pagarplus.app.network.models.createsignup.StateListResponse
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.SuccessResponse
import java.lang.ClassCastException
import java.util.*
import kotlin.collections.ArrayList

public class BranchDeptlistDialog(private val mContext: Context) : BaseDialogFragment<DialogItemlistdialogBinding>(
    R.layout.dialog_itemlistdialog) {
    private val viewModel: ItemlistVM by viewModels<ItemlistVM>()
    public var Selecteditem= Itemlistdialog1RowModel()
    private var mListener: OnCompleteListener? =null
    public var branchsize: Int? = 0
    private lateinit var recyclerItemlistdialogAdapter:RecyclerItemlistdialogAdapter

    public override fun onInitialized(): Unit {
        viewModel.navArguments = arguments
        var IsBranchDept=viewModel.navArguments!!.getBoolean(IntentParameters.IsBranchDept)

        if(IsBranchDept) {
            viewModel.callFetchBranchListApi()
            recyclerItemlistdialogAdapter = RecyclerItemlistdialogAdapter(viewModel.BranchList.value ?: mutableListOf())
            binding.recyclerItemlistdialog.adapter = recyclerItemlistdialogAdapter
            recyclerItemlistdialogAdapter.setOnItemClickListener(
                object : RecyclerItemlistdialogAdapter.OnItemClickListener {
                    override fun onItemClick(
                        view: View,
                        position: Int,
                        item: Itemlistdialog1RowModel
                    ) {
                        onClickRecyclerItemlistdialog(view, position, item)
                    }
                })
            viewModel.BranchList.observe(requireActivity()) {
                if (it != null)
                    recyclerItemlistdialogAdapter.updateData(
                        it as ArrayList<Itemlistdialog1RowModel> ?: arrayListOf()
                    )
            }
        }else{
            viewModel.callFetchDeptListApi()

            recyclerItemlistdialogAdapter =
                RecyclerItemlistdialogAdapter(viewModel.DeptList.value ?: mutableListOf())
            binding.recyclerItemlistdialog.adapter = recyclerItemlistdialogAdapter
            recyclerItemlistdialogAdapter.setOnItemClickListener(
                object : RecyclerItemlistdialogAdapter.OnItemClickListener {
                    override fun onItemClick(
                        view: View,
                        position: Int,
                        item: Itemlistdialog1RowModel
                    ) {
                        onClickRecyclerItemlistdialog(view, position, item)
                    }
                })
            viewModel.DeptList.observe(requireActivity()) {
                if (it != null)
                    recyclerItemlistdialogAdapter.updateData(
                        it as ArrayList<Itemlistdialog1RowModel> ?: arrayListOf()
                    )
            }
        }
        binding.itemlistVM = viewModel
        setUpSearchViewItemsearchListener()
    }

    public override fun setUpClicks(): Unit
    {
        binding.txtcancel.setOnClickListener{
            this.dismiss()
        }
    }

    public fun onClickRecyclerItemlistdialog(view: View, position: Int, item: Itemlistdialog1RowModel): Unit
    {
        Selecteditem=item
        this.mListener!!.onComplete(Selecteditem)
        this.dismiss()
    }

    private fun setUpSearchViewItemsearchListener(): Unit {
        binding.searchViewItemsearch.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0 : String) : Boolean {
                // Performs search when user hit
                // the search button on the keyboard
                return false
            }
            override fun onQueryTextChange(text : String) : Boolean {
                recyclerItemlistdialogAdapter.filter.filter(text)
                return false
            }
        })
    }

    public override fun addObservers(): Unit {
        var progressDialog : AlertDialog? = null
        viewModel.progressLiveData.observe(this@BranchDeptlistDialog) {
            if(it) {
                progressDialog?.dismiss()
                progressDialog = null
                progressDialog = mContext.showProgressDialog()
            } else  {
                progressDialog?.dismiss()
            }
        }

        /*bind Branch list*/
        viewModel.fetchBranchLiveData.observe(this) {
            if(it is SuccessResponse) {
                val response = it.getContentIfNotHandled()
                onSuccessFetchBranchlist(it)
            } else if(it is ErrorResponse) {
                onError(it.data ?:Exception())
            }
        }

        /*bind Dept list*/
        viewModel.fetchDeptLiveData.observe(this) {
            if(it is SuccessResponse) {
                val response = it.getContentIfNotHandled()
                onSuccessFetchDeptlist(it)
            } else if(it is ErrorResponse) {
                onError(it.data ?:Exception())
            }
        }
    }

    /*bind response for branch*/
    private fun onSuccessFetchBranchlist(response: SuccessResponse<StateListResponse>): Unit {
        viewModel.bindFetchBranchListResponse(response.data)
        branchsize = viewModel.BranchList.value?.size
        if(branchsize!! > 0){
            viewModel.BranchList.value?.add(0,Itemlistdialog1RowModel("All Branch",0,false,true,false))
        }
    }

    /*bind response for department*/
    private fun onSuccessFetchDeptlist(response: SuccessResponse<StateListResponse>): Unit {
        viewModel.bindFetchDeptListResponse(response.data)
    }

    private fun onError(exception: Exception): Unit {
        when(exception) {
            is NoInternetConnection -> {
                Snackbar.make(binding.root, exception.message?:"", Snackbar.LENGTH_LONG).show()
            }
            is HttpException -> {
                val errorBody = exception.response()?.errorBody()?.string()
                val errorObject = if (errorBody != null  && errorBody.isJSONObject()) JSONObject(errorBody)
                else JSONObject()
                Toast.makeText(this@BranchDeptlistDialog.mContext,
                MyApp.getInstance().getString(R.string.lbl_failure),
                Toast.LENGTH_LONG).show()
            }
        }
    }

    public companion object {
        public const val TAG: String = "ITEMLIST_DIALOG"
        public fun getInstance(bundle: Bundle?,context: Context): BranchDeptlistDialog {
            val fragment = BranchDeptlistDialog(context)
            fragment.arguments = bundle
            return fragment
        }
    }
    // make sure the Activity implemented it
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mListener = activity as OnCompleteListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement OnCompleteListener")
        }
    }
    interface OnCompleteListener {
        fun <T> onComplete(`object`: T)
    }
}

