package com.pagarplus.app.network

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
import com.pagarplus.app.network.models.adminreport.EmployeePaySlipResponse
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
import com.pagarplus.app.network.models.createtoken.CreateTokenResponse
import com.pagarplus.app.network.models.editprofile.UserProfileObject
import com.pagarplus.app.network.models.employeeReports.EmployeeReportRequest
import com.pagarplus.app.network.models.expense.*
import com.pagarplus.app.network.models.expense.ExpenseReportRequest
import com.pagarplus.app.network.models.expense.ExpenseReportResponse
import com.pagarplus.app.network.models.fetchMsgHistory.FetchMsgHistoryRequest
import com.pagarplus.app.network.models.fetchMsgHistory.FetchMsgHistoryResponse
import com.pagarplus.app.network.models.fetchgetbranchlist.FetchGetBranchListResponse
import com.pagarplus.app.network.models.fetchgetdepartmentlist.FetchGetDepartmentListResponse
import com.pagarplus.app.network.models.fetchgetidprooflist.FetchGetIDProofListResponse
import com.pagarplus.app.network.models.firebase_notification.FetchFirebaseNotiResponse
import com.pagarplus.app.network.models.firebase_notification.FetchFirebaseNotiResponseListItem
import com.pagarplus.app.network.models.holiday.HolidayResponse
import com.pagarplus.app.network.models.holiday.SetHolidayRequest
import com.pagarplus.app.network.models.leavelone.LeaveRequest
import com.pagarplus.app.network.models.leavelone.LoanRequest
import com.pagarplus.app.network.models.others.ApiResponse
import com.pagarplus.app.network.models.userdashboard.ProfileResponse
import com.pagarplus.app.network.models.notificationMsg.FetchInOutMsgListResponse
import com.pagarplus.app.network.models.userdashboard.BannerResponse
import com.pagarplus.app.network.models.userdashboard.UserBannerResponseItem
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*
import kotlin.String

interface RetrofitServices {
  @FormUrlEncoded
  @POST("Token")
  suspend fun createToken(
    @Field("username") username: String?,
    @Field("password") password: String?,
    @Field("grant_type") grant_type: String?): CreateTokenResponse

  @POST("api/Account/SignIn")
  suspend fun createGetLoginDetail(@Header("Authorization") authorization: String?, @Body
      createGetLoginDetailRequest: CreateGetLoginDetailRequest?): CreateGetLoginDetailResponse

  @POST("api/Account/Logout")
  suspend fun doLogout(@Query("DeviceId") deviceId: String): String

  /** User Level Apis**/

  /** Api to get Banner images for user**/
  @GET("api/Admin/GetBanners")
  suspend fun fetchUserBanners(@Query("AdminID") adminId:String): CreateCreateBannerResponse


  /** Api to Save attendance of user**/
  @POST("api/Employee/SaveAttendance")
  public suspend fun saveattendance( @Body AttendanceRequest: SaveAttendanceDataRequest?): SaveAttendanceDataResponse


  /** Api to get Feature Types for  and attendance**/
  @GET("api/Admin/GetLoginTypes/{Feature}")
  suspend fun fetchFeatureTypes(
  @Path ("Feature") Feature:String): RetroResponse

  /** Api to get Feature Types for  and attendance**/
  @GET("api/Admin/GetCheckInTypes/{UserId}")
  suspend fun fetchVisitTypes(
    @Path ("UserId") Feature:Int): RetroResponse
  /** Api to get   ExpensesTypes **/
  @GET("api/Employee/GetExpenseHeads")
  suspend fun fetchExpenseTypes(@Header("Authorization") authorization: String?): ExpenseObject

  /** Api to get   SubExpensesTypes **/
  @GET("api/Employee/GetExpenseSubTypes")
  suspend fun fetchSubExpenseTypes(@Header("Authorization") authorization: String?): ExpenseObject

  /** Api to add ExpenseDetails **/
  @POST("api/Employee/AddExpense")
  suspend fun addExpenseDetails(
    @Body createAddExpenseRequest: AddExpenseRequest?): RetroResponse

  /** Api to upload image**/
  @Streaming
  @Multipart
  @POST("api/Admin/UploadFile")
  suspend fun createUploadFile(
    @Part part: MultipartBody.Part,
    @Query("ImageType") imageType: String?): RetroResponse


  /**Leave related apis**/

  /**api to request a leave**/
  @POST("api/Employee/RequestLeave")
  suspend fun createRequestLeave(@Body createRequestLeaveRequest: LeaveRequest?): RetroResponse

  /**Loan related apis**/
  /**api to request a loan**/
  @POST("api/Employee/RequestLoan")
  suspend fun createRequestLoan(@Body createRequestLoanRequest: LoanRequest?):
          RetroResponse

  /** Api to get Employee Profile**/
  @GET("api/Employee/GetMyProfile")
  suspend fun getEmployeeProfile(@Query("ID") id: Int): ProfileResponse

  /*api to setHolidays*/
  @POST("api/Admin/SetHolidays")
  suspend fun setHoliday( @Body setHolidayRequest: SetHolidayRequest): ApiResponse

  /*api to UpdateHolidays*/
  @POST("api/Admin/UpdateHolidays")
  suspend fun updateHolidays( @Body setHolidayRequest: SetHolidayRequest): ApiResponse

  /*api to fetch holiday type list*/
  @GET("api/Admin/GetHolidayTypes")
  suspend fun getHolidayTypes(): ArrayList<FeaturesTypes>

  /** Api to get HolidaysList**/
  @GET("api/Admin/GetHolidays")
  suspend fun fetchHoliDaysList(@Query("AdminID") adminId:String): HolidayResponse

  /********************************************************************************************/
  /*committed by amruta*/
  /** Api to register admin/owners details **/
  @POST("api/Account/SignUp")
  suspend fun createSignUp(@Header("Content-type") contentType: String?,
                           @Body createSignUpRequest: CreateSignUpRequest?): CreateSignUpResponse

  /////////////common api for all//////////////////
  /*api to create branch*/
  @POST("api/Admin/CreateBranch")
  suspend fun createBranch(@Header("Content-type") contentType: String?, @Body
  createCreateBranchRequest: CreateCreateBranchRequest?): CreateCreateBranchResponse

  /*api to fetch department list*/
  @GET("api/Admin/GetDepartmentList")
  suspend fun fetchGetDepartmentList(): FetchGetDepartmentListResponse

  /*api to get branch list*/
  @GET("api/Admin/GetBranchList")
  suspend fun fetchGetBranchList(@Query("OrgID") orgID: Int?): StateListResponse

  /*api to fetch id proof list*/
  @GET("api/Admin/GetProofList")
  suspend fun fetchGetIDProofList(): FetchGetIDProofListResponse

  /*api to get state list*/
  @GET("api/Admin/GetStateList")
  suspend fun fetchStateList(): StateListResponse

  /*api to get city list*/
  @GET("api/Admin/GetCityList")
  suspend fun fetchCityList(@Query("StateID") stateID: Int?, @Query("Keyword") keyword: String?): StateListResponse

  ///////////Employee related apis//////////////////
  /*api to create employee*/
  @POST("api/Account/CreateEmployee")
  suspend fun createCreateEmployee(@Header("Content-type") contentType: String?, @Body
  createCreateEmployeeRequest: CreateCreateEmployeeRequest?): CreateCreateEmployeeResponse

  /*api to fetch emp list*/
  @GET("api/Admin/GetMyEmployees")
  suspend fun fetchEmpList(@Query("AdminID") adminID: Int?,@Query("BranchId") branchId: Int?,
                           @Query("DeptId") deptId: Int?,@Query("ListType") listType: String?): FetchGetEmpListResponse


  /*api to fetch emp details to edit*/
  @GET("api/Admin/EditEmployeeDetails")
  suspend fun adminGetEditEmpData(@Query("ID") empID: Int?): AdminEditEmployeeResponse

  /*api to delete employee by admin */
  @GET("api/Admin/DeleteEmployee")
  suspend fun adminDeleteEmp(@Query("EmpID") empID: Int?,@Query("ExistReason") existReason: String?,
                             @Query("DateOfExist") dateOfExist: String?): FetchGetEmpListResponse

  /*api to activate employee by admin */
  @GET("api/Admin/ActiveEmployee")
  suspend fun adminActiveEmp(@Query("EmpID") empID: Int?): FetchGetEmpListResponse

  /** Api to delete Holidays**/
  @GET("api/Admin/DeleteHoliday")
  suspend fun deleteHoliday(@Query("HolidayID") HolidayID:Int): ApiResponse


  /** Api to get Expense ReportList**/
  @POST("api/Admin/GetExpenseList")
  suspend fun getExpenseReportList(
    @Body expenseReportRequest: ExpenseReportRequest): ExpenseReportResponse

  /*api to update emp data*/
  @POST("api/Account/UpdateEmployee")
  suspend fun adminUpdateEmpData(@Query("ID") empID: Int?, @Header("Content-type") contentType: String?, @Body
  adminEditEmployeeRequest: AdminEditEmployeeRequest?): UpdateEmployeeResponse

  ///////////////////////Notification related apis//////////////////////
  /*api to fetch all notifiction msg list*/
  @GET("api/Admin/GetMessages")
  suspend fun fetchNotiMsgList(@Query("UserID") userID: Int?, @Query("Date") seldate: String?,
                               @Query("BranchID") branchID: Int?, @Query("DeptID") deptID: Int?): FetchInOutMsgListResponse

  /*api to fetch message history for reply model*/
  @GET("api/Admin/GetMessageHistory")
  suspend fun fetchMsgHistoryList(@Query("MainMessageID") mainMessageID: Int?): FetchMsgHistoryResponse

  /*api to create notification message*/
  @POST("api/Admin/CreateMesage")
  suspend fun createMessage(@Header("Content-type") contentType: String?, @Body
  createMessageRequest: CreateMsgRequest?): CreateMsgResponse

  /*api to create notification message*/
  @POST("api/Admin/ReplyMessage")
  suspend fun createReplyMessage(@Header("Content-type") contentType: String?, @Body
  createReplyMsgRequest: CreateReplyMsgRequest?): CreateReplyMsgResponse

  /*api to fetch emp list*/
  @GET("api/Admin/GetDepartmentWiseEmployee")
  suspend fun fetchEmpviaDeptList(@Query("AdminID") adminID: Int?, @Query("DepartmentID") deptID: Int?): GetEmpviaDeptListResponse

  //////////////Leave Loan Related API/////////////////////
  /*api to fetch leave requested by emp  list in admin*/
  @GET("api/Admin/GetLeaveRequests")
  suspend fun fetchGetleaveList(@Query("AdminID") adminID: Int?): FetchGetleaveListResponse

  /*api to fetch loan requested by emp list in admin*/
  @GET("api/Admin/GetLoanRequests")
  suspend fun fetchGetloanList(@Query("AdminID") adminID: Int?): FetchGetloanListResponse

  /*api to approve loan by admin */
  @POST("api/Admin/ApproveLoan")
  suspend fun adminApproveLoan(@Header("Content-type") contentType: String?, @Body
  approveLoanRequest: ApproveLoanRequest?): FetchGetloanListResponse

  /*api to approve leave by admin */
  @POST("api/Admin/ApproveLeave")
  suspend fun adminApproveLeave(@Header("Content-type") contentType: String?, @Body
  approveLeaveRequest: ApproveLeaveRequest?): FetchGetleaveListResponse

  /*api to reject loan by admin */
  @POST("api/Admin/RejectLoan")
  suspend fun adminRejectLoan(@Header("Content-type") contentType: String?, @Body
  approveLoanRequest: RejectLoanRequest?): FetchGetloanListResponse

  /*api to reject leave by admin */
  @POST("api/Admin/RejectLeave")
  suspend fun adminRejectLeave(@Header("Content-type") contentType: String?, @Body
  approveLeaveRequest: ApproveLeaveRequest?): FetchGetleaveListResponse

  /*api to fetch approved leave by admin in admin*/
  @POST("api/Admin/GetAllLeaveRequests")
  suspend fun fetchAllApprovedleaveList(@Header("Content-type") contentType: String?, @Body
  fetchGetleaveloanListRequest: FetchGetleaveloanListRequest?): FetchGetleaveListResponse

  /*api to fetch approved loan by admin in admin*/
  @POST("api/Admin/GetAllLoanRequests")
  suspend fun fetchAllApprovedloanList(@Header("Content-type") contentType: String?, @Body
  fetchGetleaveloanListRequest: FetchGetleaveloanListRequest?): FetchGetloanListResponse

  ///////////////admin dashboardrelated api///////////////////////
  /*api to get city list*/
  @GET("api/Admin/GetDashboardData")
  suspend fun fetchAdminDashboardList(@Query("AdminID") adminID: Int?, @Query("Date") seldate: String?,
                                      @Query("BranchID") branchID: Int?): FetchAdminDashboardListResponse

  /*send OTP*/
  @GET("api/Account/SendOTP")
  suspend fun SendOTP(@Query("MobileNumber") mobileNumber: String): String

/** Api to reject expense**/
  @POST("api/Admin/RejectExpense")
  suspend fun rejectExpense(@Body updateExpenseStatusRequest: ExpenseStatusRequest): ApiResponse

  /** Api to approve expense**/
  @POST("api/Admin/ApproveExpense")
  suspend fun approveExpense(@Body updateExpenseStatusRequest: ExpenseStatusRequest): ApiResponse

  /** Api to edit Employee profile **/
  @POST("api/Employee/UpdateMyProfile")
  suspend fun updateEmployeeProfile(@Body updateUserProfileRequest: UserProfileObject): ApiResponse

  ///////Banner api///////////////////////
  /*api to create advertisement*/
  @POST("api/Admin/CreateBanner")
  suspend fun createAdvertise(@Header("Content-type") contentType: String?, @Body
  createCreateBannerRequest: CreateCreateBannerRequest?): CreateCreateBannerResponse

  /*api to fetch all creted advertisement*/
  @GET("api/Admin/GetAdminBanners")
  suspend fun adminGetAllAdvertise(@Query("AdminID") adminID: Int?, @Query("Date") seldate: String?,
                                   @Query("BranchID") branchID: Int?, @Query("DeptID") deptID: Int?): CreateCreateBannerResponse

  /*api to create advertisement*/
  @POST("api/Admin/UpdateBanner")
  suspend fun updateeAdvertise(@Header("Content-type") contentType: String?, @Body
  createCreateBannerRequest: CreateCreateBannerRequest?): CreateCreateBannerResponse

  /*api to delete creted advertisement*/
  @GET("api/Admin/DeleteBanner")
  suspend fun deleteAdvertise(@Query("AdminID") adminID: Int?, @Query("BannerID") bannerID: Int?): CreateCreateBannerResponse

  /*api to fetch all creted advertisement*/
  @GET("api/Admin/ActiveBanner")
  suspend fun activeAdvertise(@Query("AdminID") adminID: Int?, @Query("BannerID") bannerID: Int?): CreateCreateBannerResponse

  /*api to fetch all creted advertisement*/
  @GET("api/Admin/GetAttendance")
  suspend fun adminGetAttendance(@Query("AdminID") adminID: Int?, @Query("BranchID") branchID: Int?
                              ,@Query("Date") seldate: String?, @Query("ListType") listType: String?,@Query("DeptID") deptID: Int?): AdminFetchEmpAttendanceListResponse

  /*api to fetch all creted advertisement*/
  @GET("api/Employee/GetMyAttendance")
  suspend fun GetAttendanceDeatils(@Query("EmployeeID") employeeID: Int?,@Query("FromDate") fromDate: String?,
                                   @Query("ToDate") toDate: String?): AdminFetchEmpAttendanceListResponse

  /*api to approve attendance*/
  @POST("api/Admin/ApproveAttendance")
  suspend fun GetApproveAttendance(@Query("AttendanceID") attendanceID: Int?,@Query("Status") status: String?,
                                   @Query("Comment") comment: String?): AdminFetchEmpAttendanceListResponse

  /*api to reject attendance*/
  @POST("api/Admin/RejectAttendance")
  suspend fun GetRejectAttendance(@Body attedanceApproveRejectRequest: AttedanceApproveRejectRequest?): AdminFetchEmpAttendanceListResponse

  /*api to get which all department is assigned to emp in admin*/
  @GET("api/Admin/GetAdminDepartments")
  suspend fun GetAdminDept(@Query("AdminID") adminID: Int?): StateListResponse

  /*api to get which all department is assigned to emp in admin*/
  @GET("api/Employee/GetLoginStatus")
  suspend fun getAttendanceStatus(
    @Query("EmployeeId") employeeId: Int?,
    @Query("Date") date: String?
  ): AttendanceStatusResponse


  /*1.api to get adminbranch */
  @GET("api/Admin/GetAdminBranches")
  suspend fun fetchGetAdminbranch(@Query("AdminID") adminID: Int?): AdminReportResponse

  /*2.api to get admindepartment */
  @GET("api/Admin/GetAdminDepartments")
  suspend fun fetchGetAdminDepartment(@Query("AdminID") adminID: Int?): AdminReportResponse

  /*3.api to get adminemployee */
  @GET("api/Admin/GetEmployees")
  suspend fun fetchAdminEmpList(@Query("AdminID") adminID: Int?,@Query("BranchId") branchId: Int?,
                                @Query("DeptId") deptId: Int?,@Query("Year") year: Int?): GetEmpviaDeptListResponse

  /*4.api to get employeeReport */
  @POST("Reports/GetEmployeeReport")
  suspend fun fetchReport(@Body employeeReportRequest: EmployeeReportRequest): ResponseBody

  @POST("Reports/GetWageReport")
  suspend fun fetchBReport(@Body employeeReportRequest: EmployeeReportRequest): ResponseBody

  @POST("Reports/GetYearlyLoanReport")
  suspend fun fetchCReport(@Body employeeReportRequest: EmployeeReportRequest): ResponseBody

  @POST("Reports/GetYearlyLeaveReport")
  suspend fun fetchEReport(@Body employeeReportRequest: EmployeeReportRequest): ResponseBody

  /** Api to get Employees attendance And Salary report list based On the Employee list sent**/
  @POST("api/Admin/GetAttendanceList")
  suspend fun getEmployeeSAListForAdmin(@Body adminSAReportRequest: AdminSAReportRequest): AdminReportResponse

  @GET("api/Admin/GetExpenseReport")
  suspend fun fetchExpenseAdminReportList(
     @Query("AdminID") adminID: Int?,
     @Query("BranchID") branchID: Int?,
     @Query("DeptID") deptID: Int?,
     @Query("Date") date: String?
  ): ExpenseReportResponse

  @GET("api/Admin/GetExpenseDetailsReport")
  suspend fun fetchExpenseAdminReportDetails(
    @Query("EmployeeID") EmployeeID: Int?,
    @Query("Date") date: String?): ReportDetailsResponse

  @GET("api/SalaryCalculation/GetPayslipDetails")
  suspend fun fetchEmployeePaySlipDetails(
    @Query("EmployeeID") EmployeeID: Int?,
    @Query("Month") Month: Int?,
    @Query("Year") Year: Int?): EmployeePaySlipResponse

  /*committed by amruta*/
  //approve/reject all attendance list in attendance module
  @POST("api/Admin/ApproveAllAttendance")
  suspend fun AprRejAllAttendance(@Body attedanceApproveRejectRequest: AttedanceApproveRejectRequest?): AdminFetchEmpAttendanceListResponse

  //get firebase notifications
  @GET("api/Admin/GetNotificationHistory")
  suspend fun fetchFirebaseNotiList(@Query("UserId") userId: Int?): FetchFirebaseNotiResponse
}

//const val BASE_URL: String = "http://117.205.68.9/PagarplusNewApi/"
//const val BASE_URL: String = "http://117.205.68.9/PagarwebApi/"
const val BASE_URL: String = "http://192.168.1.126/PagarwebApi/"
