package com.pagarplus.app.network.models.editprofile

import com.google.gson.annotations.SerializedName
import com.pagarplus.app.modules.editprofile.data.model.EditProfileModel
import com.pagarplus.app.network.models.adminEditEmpdata.FetchEditEmployeeDetailsResponseListItemProofsItem
import com.pagarplus.app.network.models.createcreateemployee.ProofItem


data class UserProfileObject(

    @field:SerializedName("EmpID")
    var empID: Int? = 0,

    @field:SerializedName("ProfileImageURl")
    var profileImageURl: String? = "",

    @field:SerializedName("Password")
    var password: String? = null,

    @field:SerializedName("Email")
    var email: String? = null,

    @field:SerializedName("FirstName")
    var firstName: String? = null,

    @field:SerializedName("LastName")
    var lastName: String? = null,

    @field:SerializedName("Address")
    var address: String? = null,

    @field:SerializedName("Proofs")
    var proofs: ArrayList<FetchEditEmployeeDetailsResponseListItemProofsItem>? = null,

    @field:SerializedName("Gender")
    var gender: String? = null,

    @field:SerializedName("Education")
    var education: String? = null,

    @field:SerializedName("FatherName")
    var fatherName: String? = null,

    @field:SerializedName("DOB")
    var DOB: String? = null,

    @field:SerializedName("Age")
    var Age: Int? = 0,

    @field:SerializedName("stateid")
    var stateid: Int? = 0,

    @field:SerializedName("cityid")
    var cityid: Int? = 0,

    @field:SerializedName("MobileNumber")
    var mobileNumber: String? = null,

    @field:SerializedName("OfficeEndTime")
    var officeEndTime: String? = "",
)


