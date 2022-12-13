package com.pagarplus.app.network.repository

import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.extensions.FileUploadHelper
import com.pagarplus.app.extensions.NoInternetConnection
import com.pagarplus.app.extensions.isOnline
import com.pagarplus.app.network.RetrofitServices
import com.pagarplus.app.network.models.AdminDashboard.AdminFetchEmpAttendanceListResponse
import com.pagarplus.app.network.models.AdminDashboard.FetchAdminDashboardListResponse
import com.pagarplus.app.network.models.AdminDashboard.FetchDashboardListResponseListItem
import com.pagarplus.app.network.models.AdminaGetEmplist.FetchGetEmpListResponse
import com.pagarplus.app.network.models.AdminaGetEmplist.GetEmpviaDeptListResponse
import com.pagarplus.app.network.models.AdminaGetLeaveLoanlist.*
import com.pagarplus.app.network.models.adminEditEmpdata.AdminEditEmployeeRequest
import com.pagarplus.app.network.models.adminEditEmpdata.AdminEditEmployeeResponse
import com.pagarplus.app.network.models.adminEditEmpdata.UpdateEmployeeResponse
import com.pagarplus.app.network.models.adminreport.AdminReportResponse
import com.pagarplus.app.network.models.adminreport.AdminSAReportRequest
import com.pagarplus.app.network.models.attendance.*
import com.pagarplus.app.network.models.createMessage.CreateMsgRequest
import com.pagarplus.app.network.models.createMessage.CreateMsgResponse
import com.pagarplus.app.network.models.createReplyMsg.CreateReplyMsgRequest
import com.pagarplus.app.network.models.createReplyMsg.CreateReplyMsgResponse
import com.pagarplus.app.network.models.createcreateBranch.CreateCreateBranchRequest
import com.pagarplus.app.network.models.createcreateBranch.CreateCreateBranchResponse
import com.pagarplus.app.network.models.createcreatebanner.CreateCreateBannerRequest
import com.pagarplus.app.network.models.createcreatebanner.CreateCreateBannerResponse
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeResponse
import com.pagarplus.app.network.models.creategetlogindetail.CreateGetLoginDetailRequest
import com.pagarplus.app.network.models.creategetlogindetail.CreateGetLoginDetailResponse
import com.pagarplus.app.network.models.createsignup.CreateSignUpRequest
import com.pagarplus.app.network.models.createsignup.CreateSignUpResponse
import com.pagarplus.app.network.models.createsignup.StateListResponse
import com.pagarplus.app.network.models.createsignup.UpdateSignUpRequest
import com.pagarplus.app.network.models.createtoken.CreateTokenRequest
import com.pagarplus.app.network.models.createtoken.CreateTokenResponse
import com.pagarplus.app.network.models.editprofile.UserProfileObject
import com.pagarplus.app.network.models.employeeReports.EmployeeReportRequest
import com.pagarplus.app.network.models.expense.*
import com.pagarplus.app.network.models.fetchMsgHistory.FetchMsgHistoryResponse
import com.pagarplus.app.network.models.fetchgetbranchlist.FetchGetBranchListResponse
import com.pagarplus.app.network.models.fetchgetdepartmentlist.FetchGetDepartmentListResponse
import com.pagarplus.app.network.models.fetchgetidprooflist.FetchGetIDProofListResponse
import com.pagarplus.app.network.models.holiday.HolidayResponse
import com.pagarplus.app.network.models.holiday.SetHolidayRequest
import com.pagarplus.app.network.models.leavelone.LeaveRequest
import com.pagarplus.app.network.models.leavelone.LoanRequest
import com.pagarplus.app.network.models.notificationMsg.FetchInOutMsgListResponse
import com.pagarplus.app.network.models.others.ApiResponse
import com.pagarplus.app.network.models.userdashboard.BannerResponse
import com.pagarplus.app.network.models.userdashboard.ProfileResponse
import com.pagarplus.app.network.resources.ErrorResponse
import com.pagarplus.app.network.resources.Response
import com.pagarplus.app.network.resources.SuccessResponse
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

class NetworkRepository : KoinComponent {
  private val retrofitServices: RetrofitServices by inject()

  private val errorMessage: String = "Something went wrong."

  suspend fun createGetLoginDetail(authorization: String?,
      createGetLoginDetailRequest: CreateGetLoginDetailRequest?):
      Response<CreateGetLoginDetailResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.createGetLoginDetail(authorization,
          createGetLoginDetailRequest))
    } else {
      val internetException =
          NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }
  suspend fun userLogout(userID: Int):
          Response<String> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.doLogout(userID))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  suspend fun createToken(request:CreateTokenRequest): Response<CreateTokenResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.createToken( request.username, request.password, request.grantType))
    } else {
      val internetException =
          NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

/** User Level Api Calling methods**/
  /**--------------Api calling method To save Attendance Details--------**/
  public suspend fun saveattendance(payment: SaveAttendanceDataRequest): Response<SaveAttendanceDataResponse> {
    return try {
      val isOnline = MyApp.getInstance().isOnline()
      if (isOnline) {

        SuccessResponse(retrofitServices.saveattendance(payment))
      } else {
        val internetException =
          NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
        ErrorResponse(internetException.message ?: errorMessage, internetException)
      }
    } catch (e: java.lang.Exception) {
      e.printStackTrace()
      ErrorResponse(e.message ?: errorMessage, e)
    }
  }
/**api method to get Banner images for user**/

  /*suspend fun fetchUserBanners(adminId: String?): Response<BannerResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchUserBanners(adminId?:"0"))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }*/

  /**api method to get visit types for user**/

  suspend fun fetchFeatureTypes(Feature:String): Response<RetroResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchFeatureTypes(Feature))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /**api method to get Expense types for user**/
  suspend fun fetchExpenseTypes(authorization: String?): Response<ExpenseObject> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchExpenseTypes(authorization))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /**api method to get SubExpense types for user**/
  suspend fun fetchSubExpenseTypes(authorization: String?): Response<ExpenseObject> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchSubExpenseTypes(authorization))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }
  /**api method to Add Expense details**/
  suspend fun addExpenseDetails(request:AddExpenseRequest): Response<RetroResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.addExpenseDetails(request))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /**api method to Upload a File **/
  suspend fun uploadFile(imageType: String?, filePath: Uri, fileSize: ByteArray):
          Response<RetroResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      val fileName = FileUploadHelper.getFileName(filePath)
      //val FileSize = FileUploadHelper.getFileBytes(filePath)
      Log.e("Filesize",fileSize.toString())
      val part = MultipartBody.Part.createFormData("File", fileName,
        fileSize.toRequestBody("image/*".toMediaType()))
      SuccessResponse(retrofitServices.createUploadFile(part,imageType))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

/** api Method To Request a Leave for User**/
  suspend fun requestLeave(createRequestLeaveRequest: LeaveRequest?):
          Response<RetroResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.createRequestLeave(createRequestLeaveRequest))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }


  /** api Method To Request a Loan for User**/
  suspend fun requestLoan(createRequestLoanRequest: LoanRequest?):
          Response<RetroResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.createRequestLoan(createRequestLoanRequest))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /** api to get Employee Profile**/
  suspend fun getEmployeeProfile(EmpId:Int):
          Response<ProfileResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.getEmployeeProfile(EmpId))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /**api method to get Holiday list**/
  suspend fun getHolidaysList(id:String): Response<HolidayResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchHoliDaysList(id))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /**api method to get Holiday Types**/
  suspend fun getHolidayTypes(): Response<ArrayList<FeaturesTypes>> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.getHolidayTypes())
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /**api method to set Holiday**/
  suspend fun setHoliday(setHolidayRequest: SetHolidayRequest): Response<ApiResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.setHoliday(setHolidayRequest))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }


  /*committed by amruta*/
  /*api method to crerate employee details*/
  /*retrofit function to create Admin Account*/
  suspend fun createSignUp(contentType: String?, createSignUpRequest: CreateSignUpRequest?):
          Response<CreateSignUpResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.createSignUp(contentType, createSignUpRequest))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  ////////////common apis//////////////////////////
  suspend fun fetchGetDepartmentList(): Response<FetchGetDepartmentListResponse> =
    try {
      val isOnline = MyApp.getInstance().isOnline()
      if(isOnline) {
        SuccessResponse(retrofitServices.fetchGetDepartmentList())
      } else {
        val internetException =
          NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
        ErrorResponse(internetException.message ?:errorMessage, internetException)
      }
    } catch(e:Exception) {
      e.printStackTrace()
      ErrorResponse(e.message ?:errorMessage, e)
    }

  /*fetch branch list based on login admin orgid*/
  /*used in create employee screen*/
  suspend fun fetchGetBranchList(orgID: Int?): Response<StateListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchGetBranchList(orgID))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*get ID proof dropdown from api*/
  suspend fun fetchGetIDProofList(): Response<FetchGetIDProofListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchGetIDProofList())
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*get State List api*/
  suspend fun fetchStateList(): Response<StateListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchStateList())
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*get State List api*/
  suspend fun fetchCityList(stateID: Int?, keyword: String?): Response<StateListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchCityList(stateID,keyword))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /////////////create branch api//////////////
  /*retrofit function to create branch*/
  suspend fun createCreateBranch(contentType: String?,
                                 createCreateBranchRequest: CreateCreateBranchRequest?):
          Response<CreateCreateBranchResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.createBranch(contentType,
        createCreateBranchRequest))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  ///////////Employee APi////////////////
  /*retrofit function to create Employee*/
  suspend fun createCreateEmployee(contentType: String?,
                                   createCreateEmployeeRequest: CreateCreateEmployeeRequest?):
          Response<CreateCreateEmployeeResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.createCreateEmployee(contentType,
        createCreateEmployeeRequest))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*api to get employee requested leave list admin*/
  suspend fun fetchAdminEmpList(adminID: Int?,branchID: Int?,deptID: Int?,listType: String?): Response<FetchGetEmpListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchEmpList(adminID,branchID,deptID,listType))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*delete employee by admin api*/
  suspend fun adminDeleteEmp(empID: Int?,reason: String?, existDate: String?): Response<FetchGetEmpListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.adminDeleteEmp(empID,reason,existDate))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*activate employee by admin api*/
  suspend fun adminActivateEmp(empID: Int?): Response<FetchGetEmpListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.adminActiveEmp(empID))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*get employee details to edit by admin api*/
  suspend fun admingetEditEmpData(empID: Int?): Response<AdminEditEmployeeResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.adminGetEditEmpData(empID))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*Update employee details by admin api*/
  suspend fun adminUpdateEmpData(empID: Int?, contentType: String?, adminEditEmployeeRequest: AdminEditEmployeeRequest?):
          Response<UpdateEmployeeResponse> =
    try {
      val isOnline = MyApp.getInstance().isOnline()
      if(isOnline) {
        SuccessResponse(retrofitServices.adminUpdateEmpData(empID, contentType, adminEditEmployeeRequest))
      } else {
        val internetException =
          NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
        ErrorResponse(internetException.message ?:errorMessage, internetException)
      }
    } catch(e:Exception) {
      e.printStackTrace()
      ErrorResponse(e.message ?:errorMessage, e)
    }

//////////////////Notification API///////////////////////
  /*get notification inbox msg list from api*/
  suspend fun fetchNotiMsgList(userID: Int?, seldate: String?,branchID: Int?,deptID: Int?): Response<FetchInOutMsgListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchNotiMsgList(userID,seldate,branchID, deptID))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*api to get employee requested leave list admin*/
  suspend fun fetchReplyMsgHistoryList(mainMessageID: Int?): Response<FetchMsgHistoryResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchMsgHistoryList(mainMessageID))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*retrofit function to create branch*/
  suspend fun createReplyMessage(contentType: String?,
                                 createReplyMessageRequest: CreateReplyMsgRequest?):
          Response<CreateReplyMsgResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.createReplyMessage(contentType,
        createReplyMessageRequest))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*retrofit function to create message*/
  suspend fun createMessage(contentType: String?,
                            createMessageRequest: CreateMsgRequest?):
          Response<CreateMsgResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.createMessage(contentType,
        createMessageRequest))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /////////////Loan & Leave apis////////////////////
  /*api to get employee requested loan list admin*/
  suspend fun fetchAdminLoanList(adminID: Int?): Response<FetchGetloanListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchGetloanList(adminID))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

/*api to get employee requested leave list admin*/
  suspend fun fetchAdminLeaveList(adminID: Int?): Response<FetchGetleaveListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchGetleaveList(adminID))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }
  suspend fun deleteHoliday(id:Int): Response<ApiResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.deleteHoliday(id))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }
  /*approve loan request api*/
  suspend fun adminApproveLoan(contentType: String?,
                               approveLoanRequest: ApproveLoanRequest?): Response<FetchGetloanListResponse> =
    try {
      val isOnline = MyApp.getInstance().isOnline()
      if(isOnline) {
        SuccessResponse(retrofitServices.adminApproveLoan(contentType, approveLoanRequest))
      } else {
        val internetException =
          NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
        ErrorResponse(internetException.message ?:errorMessage, internetException)
      }
    } catch(e:Exception) {
      e.printStackTrace()
      ErrorResponse(e.message ?:errorMessage, e)
    }

  suspend fun getExpenseReportList(request: ExpenseReportRequest): Response<ExpenseReportResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.getExpenseReportList(request))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }


  /*approve leave request api*/
  suspend fun adminApproveLeave(contentType: String?,
                                approveLeaveRequest: ApproveLeaveRequest?): Response<FetchGetleaveListResponse> =
    try {
      val isOnline = MyApp.getInstance().isOnline()
      if(isOnline) {
        SuccessResponse(retrofitServices.adminApproveLeave(contentType, approveLeaveRequest))
      } else {
        val internetException =
          NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
        ErrorResponse(internetException.message ?:errorMessage, internetException)
      }
    } catch(e:Exception) {
      e.printStackTrace()
      ErrorResponse(e.message ?:errorMessage, e)
    }

  /*reject loan request api*/
  suspend fun adminRejectLoan(contentType: String?,
                              rejectLoanRequest: RejectLoanRequest?): Response<FetchGetloanListResponse> =
    try {
      val isOnline = MyApp.getInstance().isOnline()
      if(isOnline) {
        SuccessResponse(retrofitServices.adminRejectLoan(contentType, rejectLoanRequest))
      } else {
        val internetException =
          NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
        ErrorResponse(internetException.message ?:errorMessage, internetException)
      }
    } catch(e:Exception) {
      e.printStackTrace()
      ErrorResponse(e.message ?:errorMessage, e)
    }

  /*reject leave request api*/
  suspend fun adminRejectLeave(contentType: String?,
                               rejectLeaveRequest: ApproveLeaveRequest?): Response<FetchGetleaveListResponse> =
    try {
      val isOnline = MyApp.getInstance().isOnline()
      if(isOnline) {
        SuccessResponse(retrofitServices.adminRejectLeave(contentType, rejectLeaveRequest))
      } else {
        val internetException =
          NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
        ErrorResponse(internetException.message ?:errorMessage, internetException)
      }
    } catch(e:Exception) {
      e.printStackTrace()
      ErrorResponse(e.message ?:errorMessage, e)
    }

  /*api to get approved leave list by admin in admin*/
  suspend fun fetchAllApprovedLeaveList(contentType: String?,
         fetchGetleaveloanListRequest: FetchGetleaveloanListRequest?): Response<FetchGetleaveListResponse> =
    try {
      val isOnline = MyApp.getInstance().isOnline()
      if(isOnline) {
        SuccessResponse(retrofitServices.fetchAllApprovedleaveList(contentType, fetchGetleaveloanListRequest))
      } else {
        val internetException =
          NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
        ErrorResponse(internetException.message ?:errorMessage, internetException)
      }
    } catch(e:Exception) {
      e.printStackTrace()
      ErrorResponse(e.message ?:errorMessage, e)
    }

  /*api to get approved loan list by admin in admin*/
  suspend fun fetchAllApprovedLoanList(contentType: String?,
         fetchGetleaveloanListRequest: FetchGetleaveloanListRequest?): Response<FetchGetloanListResponse> =
    try {
      val isOnline = MyApp.getInstance().isOnline()
      if(isOnline) {
        SuccessResponse(retrofitServices.fetchAllApprovedloanList(contentType, fetchGetleaveloanListRequest))
      } else {
        val internetException =
          NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
        ErrorResponse(internetException.message ?:errorMessage, internetException)
      }
    } catch(e:Exception) {
      e.printStackTrace()
      ErrorResponse(e.message ?:errorMessage, e)
    }

////////////create advertise apis////////////////////
  /*api to get employee names based on department*/
  suspend fun fetchEmpViaDeptList(adminID: Int?,deptID: Int?): Response<GetEmpviaDeptListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchEmpviaDeptList(adminID,deptID))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*create advertise api*/
  suspend fun createCreateBanner(contentType: String?,
                                 createCreateBannerRequest: CreateCreateBannerRequest?): Response<CreateCreateBannerResponse> =
    try {
      val isOnline = MyApp.getInstance().isOnline()
      if(isOnline) {
        SuccessResponse(retrofitServices.createAdvertise(contentType, createCreateBannerRequest))
      } else {
        val internetException =
          NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
        ErrorResponse(internetException.message ?:errorMessage, internetException)
      }
    } catch(e:Exception) {
      e.printStackTrace()
      ErrorResponse(e.message ?:errorMessage, e)
    }

  /////////admindahsboard related apis///////////////////////////
  /*get admin Dashboard api*/
  suspend fun fetchAdminDashboardList(adminID: Int?, seldate: String?,branchID:Int?): Response<FetchAdminDashboardListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchAdminDashboardList(adminID,seldate,branchID))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*get otp for registeration*/
  suspend fun AdminSendOTP(Mobile: String):
          Response<String> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.SendOTP(Mobile))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*get all created advertise list*/
  suspend fun advertiseList(adminID: Int?,seldate: String?,branchID: Int?,deptID: Int?): Response<CreateCreateBannerResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.adminGetAllAdvertise(adminID,seldate,branchID,deptID))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*update advertise api*/
  suspend fun updateBanner(contentType: String?,
                                 createCreateBannerRequest: CreateCreateBannerRequest?): Response<CreateCreateBannerResponse> =
    try {
      val isOnline = MyApp.getInstance().isOnline()
      if(isOnline) {
        SuccessResponse(retrofitServices.updateeAdvertise(contentType, createCreateBannerRequest))
      } else {
        val internetException =
          NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
        ErrorResponse(internetException.message ?:errorMessage, internetException)
      }
    } catch(e:Exception) {
      e.printStackTrace()
      ErrorResponse(e.message ?:errorMessage, e)
    }

  /*delete advertise by admin api*/
  suspend fun deleteAdvertise(adminID: Int?,bannerID: Int?): Response<CreateCreateBannerResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.deleteAdvertise(adminID,bannerID))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*Active advertise by admin api*/
  suspend fun activeAdvertise(adminID: Int?,bannerID: Int?): Response<CreateCreateBannerResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.activeAdvertise(adminID,bannerID))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /** Api Method To reject Expenses**/
  suspend fun rejectExpense(updateExpenseStatusRequest: ExpenseStatusRequest):
          Response<ApiResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.rejectExpense(updateExpenseStatusRequest))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /** Api Method To approve Expenses**/
  suspend fun approveExpense(updateExpenseStatusRequest: ExpenseStatusRequest):
          Response<ApiResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.approveExpense(updateExpenseStatusRequest))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /** Api Method To approve Expenses**/
  suspend fun updateUserProfile(updateUserProfileRequest: UserProfileObject):
          Response<ApiResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.updateEmployeeProfile(updateUserProfileRequest))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*get all attendance data in admin dashboard*/
  suspend fun adminGetAttendance(adminID: Int?,branchID: Int?,seldate: String?,listType:String?,deptID: Int?):
          Response<AdminFetchEmpAttendanceListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.adminGetAttendance(adminID,branchID,seldate,listType,deptID))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /*get all attendance data in admin dashboard*/
  suspend fun GetAttendanceDetails(empID: Int?,fromDate: String?,toDate:String?):
          Response<AdminFetchEmpAttendanceListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.GetAttendanceDeatils(empID,fromDate,toDate))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /** Api Method To approve Attendance**/
  suspend fun approveAttendance(attedanceApproveRejectRequest: AttedanceApproveRejectRequest?):
          Response<AdminFetchEmpAttendanceListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.GetApproveAttendance(attedanceApproveRejectRequest))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  /** Api Method To reject Attendance**/
  suspend fun rejectAttendance(attedanceApproveRejectRequest: AttedanceApproveRejectRequest?):
          Response<AdminFetchEmpAttendanceListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.GetRejectAttendance(attedanceApproveRejectRequest))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  suspend fun fetchGetAdminDeptList(adminID: Int?): Response<StateListResponse> =
    try {
      val isOnline = MyApp.getInstance().isOnline()
      if(isOnline) {
        SuccessResponse(retrofitServices.GetAdminDept(adminID))
      } else {
        val internetException =
          NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
        ErrorResponse(internetException.message ?:errorMessage, internetException)
      }
    } catch(e:Exception) {
      e.printStackTrace()
      ErrorResponse(e.message ?:errorMessage, e)
    }
  suspend fun fetchAttendanceStatus(userID: Int?,Date:String): Response<AttendanceStatusResponse> =
    try {
      val isOnline = MyApp.getInstance().isOnline()
      if(isOnline) {
        SuccessResponse(retrofitServices.getAttendanceStatus(userID,Date))
      } else {
        val internetException =
          NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
        ErrorResponse(internetException.message ?:errorMessage, internetException)
      }
    } catch(e:Exception) {
      e.printStackTrace()
      ErrorResponse(e.message ?:errorMessage, e)
      }

  //*used to get admin branch*//*
  suspend fun fetchGetAdminbranch(adminID: Int?): Response<AdminReportResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchGetAdminbranch(adminID))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }


  /*used to get admin department*/
  suspend fun fetchGetAdminDepartment(adminID: Int?): Response<AdminReportResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchGetAdminDepartment(adminID))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  // to get admin employee list //
  suspend fun fetchAdminEmployeesList(adminID: Int?,branchID: Int?,deptID: Int?,year: String?): Response<GetEmpviaDeptListResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchAdminEmpList(adminID,branchID,deptID,year))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }



  // to get admin employee list //
  suspend fun fetchEmployeeSAListForAdmin(adminSAReportRequest: AdminSAReportRequest): Response<AdminReportResponse> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.getEmployeeSAListForAdmin(adminSAReportRequest))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  // to get report of employee //
  suspend fun fetchReport(request: EmployeeReportRequest): Response<ResponseBody> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchReport(request))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }


  suspend fun fetchBReport(request: EmployeeReportRequest): Response<ResponseBody> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchBReport(request))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  suspend fun fetchCReport(request: EmployeeReportRequest): Response<ResponseBody> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchCReport(request))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }

  suspend fun fetchEReport(request: EmployeeReportRequest): Response<ResponseBody> = try {
    val isOnline = MyApp.getInstance().isOnline()
    if(isOnline) {
      SuccessResponse(retrofitServices.fetchEReport(request))
    } else {
      val internetException =
        NoInternetConnection(MyApp.getInstance().getString(R.string.no_internet_connection))
      ErrorResponse(internetException.message ?:errorMessage, internetException)
    }
  } catch(e:Exception) {
    e.printStackTrace()
    ErrorResponse(e.message ?:errorMessage, e)
  }
}
