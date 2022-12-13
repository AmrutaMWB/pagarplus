package com.pagarplus.app.modules.signup.`data`.viewmodel

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.extensions.extractTimeto24
import com.pagarplus.app.modules.signup.data.model.SignUpModel
import com.pagarplus.app.network.models.attendance.RetroResponse
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import com.pagarplus.app.network.models.createsignup.CreateSignUpRequest
import com.pagarplus.app.network.models.createsignup.CreateSignUpResponse
import com.pagarplus.app.network.models.createsignup.UpdateSignUpRequest
import com.pagarplus.app.network.models.editprofile.UserProfileObject
import com.pagarplus.app.network.models.others.ApiResponse
import com.pagarplus.app.network.repository.NetworkRepository
import com.pagarplus.app.network.resources.Response
import kotlin.Boolean
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.SimpleDateFormat

class SignUpVM : ViewModel(), KoinComponent {
    val signUpModel: MutableLiveData<SignUpModel> = MutableLiveData(SignUpModel())

    var navArguments: Bundle? = null

    val progressLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    private val networkRepository: NetworkRepository by inject()

    val createSignUpLiveData: MutableLiveData<Response<CreateSignUpResponse>> =
        MutableLiveData<Response<CreateSignUpResponse>>()

    val updateProfileLiveData: MutableLiveData<Response<ApiResponse>> =
        MutableLiveData<Response<ApiResponse>>()

    val GetOTPLiveData: MutableLiveData<Response<String>> =
        MutableLiveData<Response<String>>()

    private val prefs: PreferenceHelper by inject()

    val userdetails = prefs.getLoginDetails<LoginResponse>()
    val profiledetails = prefs.getProfileDetails<CreateCreateEmployeeRequest>()

    val imageUploadLiveData: MutableLiveData<Response<RetroResponse>> = MutableLiveData<Response<RetroResponse>>()
    val formatter = SimpleDateFormat("HH:mm:ss")

    /*call image upload api*/
    fun ImageUpload(folder:String,uri: Uri,imgsize: ByteArray) {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            imageUploadLiveData.postValue(
                networkRepository.uploadFile(folder,uri,imgsize)
            )
            progressLiveData.postValue(false)
        }
    }

    /*call loan list api*/
    fun callSendOTP() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            GetOTPLiveData.postValue(
                networkRepository.AdminSendOTP(
                    Mobile = signUpModel.value?.etMobileValue!!
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun callCreateSignUpApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            createSignUpLiveData.postValue(
                networkRepository.createSignUp(
                    contentType = """application/json""",
                    createSignUpRequest = CreateSignUpRequest(
                        organization = signUpModel.value?.etFirmNameValue,
                        email = signUpModel.value?.etEmailIDValue,
                        address = signUpModel.value?.etAddressValue,
                        firstName = signUpModel.value?.etFirstNameValue,
                        state = signUpModel.value?.StateID,
                        lastName = signUpModel.value?.etLastNameValue,
                        city = signUpModel.value?.cityID,
                        mobile = signUpModel.value?.etMobileValue,
                        password = signUpModel.value?.etPasswordValue,
                        confirmPassword = signUpModel.value?.etConfirmPassworValue,
                        referralCode = signUpModel.value?.etReferralcode,
                        officeStartTime = signUpModel.value?.etOffOpentime?.extractTimeto24(),
                        officeEndTime = signUpModel.value?.etOffEndtime?.extractTimeto24())
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun bindCreateSignUpResponse(response: CreateSignUpResponse) {
        val signUpModelValue = signUpModel.value ?:SignUpModel()
        signUpModel.value = signUpModelValue
    }

    /*update admin profile call api*/
    fun callUpdateProfileApi() {
        viewModelScope.launch {
            progressLiveData.postValue(true)
            updateProfileLiveData.postValue(
                networkRepository.updateUserProfile(
                    updateUserProfileRequest = UserProfileObject(
                        email = signUpModel.value?.etEmailIDValue,
                        mobileNumber = signUpModel.value?.etMobileValue,
                        address = signUpModel.value?.etAddressValue,
                        firstName = signUpModel.value?.etFirstNameValue,
                        stateid = signUpModel.value?.StateID,
                        lastName = signUpModel.value?.etLastNameValue,
                        cityid = signUpModel.value?.cityID,
                        password = signUpModel.value?.etPasswordValue,
                        empID = userdetails?.userID,
                        DOB = signUpModel.value?.etDOB,
                        Age = 0,
                        education = "",
                        gender = "",
                        fatherName = "",
                        proofs = signUpModel.value?.proofs,
                        profileImageURl = signUpModel.value?.etProfileImgURL,
                        officeStartTime = signUpModel.value?.etOffOpentime?.extractTimeto24(),
                        officeEndTime = signUpModel.value?.etOffEndtime?.extractTimeto24()
                    )
                )
            )
            progressLiveData.postValue(false)
        }
    }

    fun bindUpdateProfileResponse(response: ApiResponse) {
        val signUpModelValue = signUpModel.value ?:SignUpModel()
        signUpModel.value = signUpModelValue
    }
}
