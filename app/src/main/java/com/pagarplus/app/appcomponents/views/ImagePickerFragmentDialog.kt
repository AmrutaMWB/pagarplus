package com.pagarplus.app.appcomponents.views

import android.Manifest.permission.*
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pagarplus.app.R
import com.pagarplus.app.databinding.LayoutImagePickerDialogBinding
import com.pagarplus.app.extensions.PickerHandler
import com.pagarplus.app.extensions.PickerHandler.OPEN_CAMERA_REQUEST_CODE
import com.pagarplus.app.extensions.PickerHandler.OPEN_STORAGE_REQUEST_CODE
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog


class ImagePickerFragmentDialog (var isGalleryRequired:Boolean=true): BottomSheetDialogFragment(),
    EasyPermissions.PermissionCallbacks {
    private var binding: LayoutImagePickerDialogBinding? = null
    private var imageUri: Uri? = null

    private lateinit var onItemFoundOrSelected: (Uri?) -> Unit
    private var onPermissionResultCallback: ((Boolean) -> Unit)? = null

    private fun openCamera() {
        imageUri = PickerHandler.createUri(context)
        val takePicture = PickerHandler.takePictureFromCameraIntent(imageUri)

        executeWithContext {

            if (PickerHandler.isValidCameraIntent(takePicture)) {
                startActivityForResult(takePicture, OPEN_CAMERA_REQUEST_CODE)
               this.startActivityForResult(takePicture, OPEN_CAMERA_REQUEST_CODE)
            } else {
                //Handle Camera not found code
                dismiss()
            }
        }
    }
    private fun openGallery() {
        val pickFile = PickerHandler.filePickerIntent(PickerHandler.MIME_TYPE_IMAGE)
        startActivityForResult(pickFile, OPEN_STORAGE_REQUEST_CODE)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        this.retainInstance = true
        initBinding()
        binding?.root?.let { dialog.setContentView(it) }
        return dialog
    }

    private fun initBinding() {
        context ?: return
        //if context is not null

        var galleryVisibility=if(isGalleryRequired) View.VISIBLE else View.GONE
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_image_picker_dialog, null, false)
        binding?.galleryLayout?.visibility=galleryVisibility

        binding?.textCamera?.setOnClickListener {
            val permissions = arrayOf(CAMERA)
           // val permissions = arrayOf(CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
            if (!hasPermission(*permissions)) {
                requestPermission(*permissions, message = "Camera Permission Needed",) {
                    val allGranted = hasPermission(*permissions)
                    //we have to consider this because one of this is true at that time this callback invoke
                    //so we have to make sure about both the condition are true
                    if (it && allGranted) {
                        Log.e("Imagepath","opening cameraa...")

                        openCamera()

                    } else {
                        dismiss()
                    }
                }
            } else {
                openCamera()
            }
        }

        binding?.textGallery?.setOnClickListener {

            if(Build.VERSION.SDK_INT>32){
                if (!hasPermission(READ_MEDIA_IMAGES)) {
                    requestPermission(READ_MEDIA_IMAGES) {
                        if (it) {
                            openGallery()
                        } else {
                            dismiss()
                        }
                    }
                } else {
                    openGallery()
                }
            }
          else {
                if (!hasPermission(READ_EXTERNAL_STORAGE)) {
                    requestPermission(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE) {
                        if (it) {
                            openGallery()
                        } else {
                            dismiss()
                        }
                    }
                } else {
                    openGallery()
                }
            }
        }
    }


    fun show(supportFragmentManager: FragmentManager, callback: (path: Uri?) -> Unit)
    {
        onItemFoundOrSelected = {
            callback(it)
        }
        val oldDialog = supportFragmentManager.findFragmentByTag("ImagePickerDialog")
        if (oldDialog != null) {
            (oldDialog as? ImagePickerFragmentDialog)?.dismiss()
        }
        show(supportFragmentManager, "ImagePickerDialog")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, `data`: Intent?): Unit {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            OPEN_CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    onItemFoundOrSelected(imageUri)
                    Log.e("Imagepath","${imageUri!!.path}")
                    dismiss()

                } else {
                    dismiss()
                }
            }
            OPEN_STORAGE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val filePath: Uri? = data?.data
                    onItemFoundOrSelected(filePath)
                    Log.e("Imagepath","storage..${filePath!!.path}")
                    dismiss()
                } else {
                    dismiss()
                }
            }
        }
    }

    private inline fun executeWithContext(callback: (Context) -> Unit) {
        context?.let { callback(it) }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ): Unit {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PickerHandler.REQUEST_PERMISSION -> {
                EasyPermissions.onRequestPermissionsResult(
                    requestCode, permissions, grantResults,
                    this@ImagePickerFragmentDialog
                )
            }
        }
    }

    private fun hasPermission(vararg permissions: String): Boolean {
        var checkPermission = false
        executeWithContext {
            checkPermission =
                EasyPermissions.hasPermissions(it, *permissions)
        }
        return checkPermission
    }

    private fun requestPermission(
        vararg permissions: String,
        message: String = "This Application need Permission",
        onPermissionResultCallback: (Boolean) -> Unit
    ) {
        EasyPermissions.requestPermissions(
            this@ImagePickerFragmentDialog,
            message,
            PickerHandler.REQUEST_PERMISSION,
            *permissions
        )
        this.onPermissionResultCallback = onPermissionResultCallback
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(
                this@ImagePickerFragmentDialog,
                *perms.toTypedArray()
            )
        ) {
            executeWithContext {
                SettingsDialog.Builder(it).build().show()
            }
        }
        onPermissionResultCallback?.invoke(false)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        onPermissionResultCallback?.invoke(true)
    }

}