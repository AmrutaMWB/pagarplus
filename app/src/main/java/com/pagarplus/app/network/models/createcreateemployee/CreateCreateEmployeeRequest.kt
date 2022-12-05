package com.pagarplus.app.network.models.createcreateemployee

import com.google.gson.annotations.SerializedName
import com.pagarplus.app.modules.editprofile.data.model.EditProfileModel
import com.pagarplus.app.network.models.editprofile.UserProfileObject

data class CreateCreateEmployeeRequest(

	@field:SerializedName("AdminID")
	val adminID: Int? = 0,

	@field:SerializedName("ProfileImageURl")
	val profileImageURl: String? = "",

	@field:SerializedName("Email")
	val email: String? = "",

	@field:SerializedName("Address")
	val address: String? = "",

	@field:SerializedName("FirstName")
	val firstName: String? = "",

	@field:SerializedName("StateID")
	val stateID: Int? = 0,

	@field:SerializedName("State")
	val state: String? = "",

	@field:SerializedName("OldBalance")
	val oldBalance: String? = "",

	@field:SerializedName("BranchID")
	val branchID: Int? = 0,

	@field:SerializedName("City")
	val city: String? = "",

	@field:SerializedName("CityID")
	val cityID: Int? = 0,

	@field:SerializedName("ConfirmPassword")
	val confirmPassword: String? = "",

	@field:SerializedName("BasicSalary")
	val basicSalary: String? = "",

	@field:SerializedName("EmpID")
	val empID: Int? = 0,

	@field:SerializedName("OrgID")
	val orgID: Int? = 0,

	@field:SerializedName("ProofTypeID")
	val proofTypeID: String? = "",

	@field:SerializedName("FrontImage")
	val frontImage: String? = "",

	@field:SerializedName("BackImage")
	val BackImage: String? = "",

	@field:SerializedName("MobileNumber")
	val mobileNumber: String? = "",

	@field:SerializedName("NoOfSickLeave")
	val noOfSickLeave: String? = "",

	@field:SerializedName("DepartmentID")
	val departmentID: Int? = 0,

	@field:SerializedName("LastName")
	val lastName: String? = "",

	@field:SerializedName("NoOfPaidLeave")
	val noOfPaidLeave: String? = "",

	@field:SerializedName("Password")
	val password: String? = "",

	@field:SerializedName("AdminBalance")
	val adminBalance: String? = "",

	@field:SerializedName("AdvanceAmount")
	val loanAdvance: String? = "",

	@field:SerializedName("LoanBalance")
	val salaryBalance: String? = "",

	@field:SerializedName("Deduction")
	val deduction: String? = "",

	@field:SerializedName("Comment")
	val comment: String? = "",

	@field:SerializedName("Designation")
	val designation: String? = "",

	@field:SerializedName("DateOfJoining")
	val dateOfJoining: String? = "",

	@field:SerializedName("Organization")
	val organization: String? = "",

	@field:SerializedName("EmergencyNumber")
	var emergencyNumber: String? = "",

	@field:SerializedName("BloodGroup")
	var bloodGroup: String? = "",

	@field:SerializedName("OfficeStartTime")
	var officeStartTime: String? = "",

	@field:SerializedName("OfficeEndTime")
	var officeEndTime: String? = "",

	@field:SerializedName("Proofs")
	val proofs: ArrayList<ProofItem>? = null,
)

data class ProofItem(
	@field:SerializedName("ProofTypeID")
	val prooftypeID: String? = "",

	@field:SerializedName("FrontImageUrl")
	val frontImage: String? = "",

	@field:SerializedName("BackImageUrl")
	val BackImage: String? = "",

	@field:SerializedName("ProofNumber")
	val ProofNumber: String? = ""
)




