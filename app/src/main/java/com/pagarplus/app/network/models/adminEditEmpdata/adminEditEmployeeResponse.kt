package com.pagarplus.app.network.models.adminEditEmpdata

import com.google.gson.annotations.SerializedName
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.extensions.extractTimeAMPM
import com.pagarplus.app.modules.editemployeedetails.data.model.EditemployeedetailsModel
import com.pagarplus.app.modules.editprofile.data.model.EditProfileModel
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest

data class AdminEditEmployeeResponse(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("List")
	val list: List<FetchEditEmployeeDetailsResponseListItem?>? = null
)

data class FetchEditEmployeeDetailsResponseListItem(

	@field:SerializedName("EmergencyNumber")
	var emergencyNumber: String? = "",

	@field:SerializedName("BloodGroup")
	var bloodGroup: String? = "",

	@field:SerializedName("OfficeStartTime")
	var officeStartTime: String? = "",

	@field:SerializedName("OfficeEndTime")
	var officeEndTime: String? = "",

	@field:SerializedName("Gender")
	var gender: String? = "",

	@field:SerializedName("Education")
	var education: String? = "",

	@field:SerializedName("FatherName")
	var fatherName: String? = "",

	@field:SerializedName("DOB")
	var DOB: String? = null,

	@field:SerializedName("Age")
	var Age: String? = null,

	@field:SerializedName("Organization")
	val organization: String? = "",

	@field:SerializedName("AdminID")
	val adminID: Int? = 0,

	@field:SerializedName("Branch")
	val branch: String? = "",

	@field:SerializedName("Department")
	val department: String? = "",

	@field:SerializedName("LoanAdvanceBalance")
	val loanAdvanceBalance: Int? = 0,

	@field:SerializedName("Email")
	val email: String? = "",

	@field:SerializedName("Address")
	val address: String? = "",

	@field:SerializedName("Deduction")
	val deduction: Int? = 0,

	@field:SerializedName("BranchID")
	val branchID: Int? = 0,

	@field:SerializedName("ProfileImageURl")
	val profileImageURl: String? = "",

	@field:SerializedName("OrgID")
	val orgID: Int? = 0,

	@field:SerializedName("OldSalaryBalance")
	val oldSalaryBalance: Int? = 0,

	@field:SerializedName("DepartmentID")
	val departmentID: Int? = 0,

	@field:SerializedName("Password")
	val password: String? = "",

	@field:SerializedName("EmployeeBalance")
	val employeeBalance: Int? = 0,

	@field:SerializedName("Designation")
	val designation: String? = "",

	@field:SerializedName("Comment")
	val comment: String? = "",

	@field:SerializedName("AdminBalance")
	val adminBalance: Int? = 0,

	@field:SerializedName("FirstName")
	val firstName: String? = "",

	@field:SerializedName("StateID")
	val stateID: Int? = 0,

	@field:SerializedName("BasicSalary")
	val basicSalary: Int? = 0,

	@field:SerializedName("Logo")
	val logo: String? = "",

	@field:SerializedName("ConfirmPassword")
	val confirmPassword: String? = "",

	@field:SerializedName("MobileNumber")
	val mobileNumber: String? = "",

	@field:SerializedName("DateOfJoining")
	val dateOfJoining: String? = "",

	@field:SerializedName("NoOfSickLeave")
	val noOfSickLeave: String? = "",

	@field:SerializedName("CityID")
	val cityID: Int? = 0,

	@field:SerializedName("LastName")
	val lastName: String? = "",

	@field:SerializedName("EmpID")
	val empID: Int? = 0,

	@field:SerializedName("NoOfPaidLeave")
	val noOfPaidLeave: String? = "",

	@field:SerializedName("State")
	val state: String? = "",

	@field:SerializedName("City")
	val city: String? = "",

	@field:SerializedName("Proofs")
	val proofs: ArrayList<FetchEditEmployeeDetailsResponseListItemProofsItem>? = null,
)

data class FetchEditEmployeeDetailsResponseListItemProofsItem(

	@field:SerializedName("ProofNumber")
	var proofNumber: String? = "",

	@field:SerializedName("FrontImageUrl")
	val frontImageUrl: String? = "",

	@field:SerializedName("BackImageUrl")
	val backImageUrl: String? = "",

	@field:SerializedName("ProofTypeID")
	val proofTypeID: String? = "",

	@field:SerializedName("ProofID")
	val proofID: Int? = 0,

	@field:SerializedName("ProofTypeName")
	val proofTypeName: String? = ""
)

fun FetchEditEmployeeDetailsResponseListItem.toEditemployeedetailsModel():EditemployeedetailsModel{
	return EditemployeedetailsModel(txtEmpId = this.empID, etEdtTxtFirstnameValue = this.firstName,
		etEdtTxtLastnameValue = this.lastName, etEdtTxtemailValue = this.email, etEdtTxtmobileNoValue = this.mobileNumber,
		etEdtTxtaddress = this.address, etEdtTxtSalaryValue = this.basicSalary.toString(), txtBranchId = this.branchID,
		txtDeptId = departmentID, etEdtTxtPaidleaveValue = this.noOfPaidLeave,
		etEdtTxtSickleaveValue = this.noOfSickLeave, txtSelectBranch = this.branch,
		txtSelectDepartme = this.department, Prooflist = this.proofs, etEdtTxtdesignation = this.designation,
		etEdtTxtdatofjoining = this.dateOfJoining, etEdtTxtState = this.state, etEdtTxtCity = this.city,
		etEdtTxtBloodgroup = this.bloodGroup, etEdtTxtEmergencynum = this.emergencyNumber,
		etEdtTxtcheckintime = this.officeStartTime?.extractTimeAMPM(), etEdtTxtcheckouttime = this.officeEndTime?.extractTimeAMPM(),
		etEdtTxtPwdValue = this.password, etEdtTxtcnfPwdValue = this.password, txtOldPassword = this.password,
		txtStateID = this.stateID, txtCityID = this.cityID)
}


fun FetchEditEmployeeDetailsResponseListItem.toUserProfile(): EditProfileModel {
	return EditProfileModel(
		etFirstname = this.firstName?:"",
		etLastName = this.lastName?:"",
		imgProfileUrl = this.profileImageURl?:"",
		etEmail = this.email,
		etAddress = this.address?:"",
		etPassword = this.password?:"",
		proofs = this.proofs,
		EmpId = this.empID?:0
	)
}
