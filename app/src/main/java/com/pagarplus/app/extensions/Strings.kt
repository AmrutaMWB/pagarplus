package com.pagarplus.app.extensions

import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pagarplus.app.R
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.Exception

/**
 * Strings file contains string utils and extension methods
 */

/**
 * Minimum 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character
 */
private val PASSWORD_PATTERN =
    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[`~#@$!%^*?&()+-_=<>,./';:{}|])[A-Za-z\\d`~#@$!%^*?&()+-_=<>,./';:{}|]{8,}$"
private val ChequeNumPattern : Pattern = Pattern.compile("^[0-9][0-9]{5,8}$")
private val AccNumPattern : Pattern = Pattern.compile("^\\d{9,16}\$")
private val IfscPattern : Pattern = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}\$")
private  val VEHICLE_PATTERN=Pattern.compile("^[A-Z]{2}[0-9]{1,2}(?: [A-Z])?(?:[A-Z]*)?[0-9]{4}$")
private val MOBILE_PATTERN  = Pattern.compile("^[6-9]{1}[0-9]{9}\$")
private val Adhar_pattern  = Pattern.compile("^[0-9]{12}\$")
private val Pan_pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}")
private val DL_pattern = Pattern.compile("^([A-Z]{2})(\\d{2})(\\d{4})(\\d{7})\$")
private val Email_pattern = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})\$")

/**
 * the string extension method to identify the value is type of email pattern or not
 * @return boolean value if string value not match with the email pattern it returns false
 */
fun String.isEmail(): Boolean {
    return Email_pattern.matcher(this).matches()
}
fun String.isPhone(): Boolean {
    return MOBILE_PATTERN.matcher(this).matches()
}

fun String.isIfsc(): Boolean {
    return IfscPattern.matcher(this).matches()
}

fun String.isAcNo(): Boolean {
    return (AccNumPattern.matcher(this).matches() &&  (this.toLongOrNull()?:0>0))
}
fun String.isCheque(): Boolean {
    return ChequeNumPattern.matcher(this).matches()
}
fun String.isVehicle(): Boolean {
    return VEHICLE_PATTERN.matcher(this).matches()
}
fun String.isPassword(): Boolean {
    return Pattern.compile(PASSWORD_PATTERN).matcher(this).matches()
}

/*commited by amruta*/
fun String.isAdhar(): Boolean {
    return Adhar_pattern.matcher(this).matches()
}
fun String.isPan(): Boolean {
    return Pan_pattern.matcher(this).matches()
}
fun String.isDL(): Boolean {
    return DL_pattern.matcher(this).matches()
}
fun String.isRequired(): Boolean {
    return true
}

/**
 * the string extension method to identify the jsonObject from the json string
 * @return boolean value if string value is not match with the jsonObject it returns false
 */
fun String.isJSONObject():Boolean{
    return try {
        JSONObject(this)
        true
    }catch (e:Exception){
        false
    }
}

fun String.extractDate():String{
    return try {
      if(this.contains("T"))
      {
          val date= this.split("T")[0]
          val array:List<String>
          if(date.contains("/"))
          { array=date.split("/")
             "${array.get(2)}/${array.get(1)}/${array.get(0)}"
          }else
          {
              array=date.split("-")
              "${array.get(2)}-${array.get(1)}-${array.get(0)}"
          }
      }
        else
            this
    }catch (e:Exception){
        this
    }
}

fun String.extractDateWith12():String{
    return try {
        val serverpattern = "dd/MM/yyyy HH:mm:ss"
        val serverDateFormat = SimpleDateFormat(serverpattern, Locale.getDefault())

        val userPattern = "dd-MM-yyyy hh:mm a"
        val userDateFormat = SimpleDateFormat(userPattern, Locale.getDefault())

        val defaultDate = serverDateFormat.parse(this)
        if ( defaultDate != null ) {
            userDateFormat.format(defaultDate)
        }else{
            ""
        }
    }catch (e:Exception){
        "N/A"
    }
}

fun String.extractDateWithT():String{
    return try {
        val serverpattern = "yyyy-MM-dd'T'HH:mm:ss"
        val serverDateFormat = SimpleDateFormat(serverpattern, Locale.getDefault())

        val userPattern = "dd-MM-yyyy hh:mm a"
        val userDateFormat = SimpleDateFormat(userPattern, Locale.getDefault())

        val defaultDate = serverDateFormat.parse(this)
        if ( defaultDate != null ) {
            userDateFormat.format(defaultDate)
        }else{
            ""
        }
    }catch (e:Exception){
        "N/A"
    }
}

fun String.extractTimeto24():String{
    return try {
        val serverpattern = "hh:mm a"
        val serverDateFormat = SimpleDateFormat(serverpattern, Locale.getDefault())

        val userPattern = "HH:mm:ss"
        val userDateFormat = SimpleDateFormat(userPattern, Locale.getDefault())

        val defaultDate = serverDateFormat.parse(this)
        if ( defaultDate != null ) {
            userDateFormat.format(defaultDate)
        }else{
            ""
        }
    }catch (e:Exception){
        "N/A"
    }
}

fun String.extractTimeAMPM():String{
    return try {
        val serverpattern = "HH:mm:ss"
        val serverDateFormat = SimpleDateFormat(serverpattern, Locale.getDefault())

        val userPattern = "hh:mm a"
        val userDateFormat = SimpleDateFormat(userPattern, Locale.getDefault())

        val defaultDate = serverDateFormat.parse(this)
        if ( defaultDate != null ) {
            userDateFormat.format(defaultDate)
        }else{
            ""
        }
    }catch (e:Exception){
        "N/A"
    }
}

fun String.getMonthId():Int{
    return try {
        var monthId=0
        if(this.contains("-")) {
            monthId= (this.split("-")[1]).toIntOrNull()?:0
        } else   if(this.contains("/"))
            monthId= (this.split("/")[1]).toIntOrNull()?:0

           monthId
    }catch (e:Exception){
        0
    }
}


fun String.getDate( format:String="dd/MM/yyyy"): Date {
    return try {
        val formatter = SimpleDateFormat(format)
        val date:Date= formatter.parse(this) as Date
        date
    }catch (e:Exception){
        Date()
    }
}
object URLParameters {
    const val Visit = "Visit"
    const val Period = "Period"
    const val Leave = "Leave"
    const val DNS = "DNS"
    const val ExpenseTypes = "ExpenseType"
    const val LocalExpenses = "Expenses"

}
object IntentParameters {

    const val UserId = "UserId"
    const val AdminId = "AdminId"
    const val UserDetails = "UserDetails"
    const val isAdmin = "isAdmin"
    const val isEmployee = "isEmployee"
    const val FromDate = "FromDate"
    const val ToDate = "ToDate"

    /*committed by amruta*/
    const val IsLeaveOrLoan = "IsLeaveOrLoan"
    const val mainMessageId = "MainMessageID"
    const val fromEmpID = "FromEmpID"
    const val EmpID = "EmpID"
    const val IsStateOrCity = "IsStateOrCity"
    const val StateId = "StateId"
    const val CityKeyword = "CityKeyword"
    const val BannerID = "BannerId"
    const val imagePath = "Imagepath"
    const val TxtMessage = "TxtMessage"
    const val TxtTitle = "TxtTitle"
    const val fromDate = "fromDate"
    const val toDate = "toDate"
    const val IsEditAdvertise = "IsEditAdvertise"
    const val IsNevigate = "IsNevigate"
    const val IsFirstLogin = "IsFirstLogin"
    const val IsBranchDept = "IsBranchDept"
    const val AllEmp = "AllEmp"
    const val IsPresentAbsent = "IsPresentAbsent"
    const val selDate = "selDate"
    const val BranchID = "BranchID"
    const val DeptID = "DeptID"
    const val Branch = "Branch"
    const val Dept = "Dept"
    const val ExpenseObject = "ExpenseObject"
    const val ExpenseId = "ExpenseId"
    const val ExpenseName = "ExpenseName"
    const val EmpName = "EmpName"
    const val FormType = "FormType"
    const val IsSalaryReport = "IsSalaryReport"
}

object ImageFolders{
    const val Expense = "Expense"
    const val Attendance = "Attendance"
    const val Loan="Loan"

    /*committed by amruta*/
    const val Idproof = "IDProof"
    const val NotificationMessage = "Notification"
    const val AdvertiseImages = "Banner"
    const val Profile = "Profile"
    const val Advertisment="Advertisment"
}

object PagarStatus{
    const val Pending = "Pending"
    const val Approved = "Approved"
    const val Rejected="Rejected"
}

/*textfield validations*/
fun validateRequired(inputlayout: TextInputLayout,editText: TextInputEditText): Boolean {
    if (editText.text.toString().trim().isEmpty()) {
        inputlayout.error = "Required Field!"
        editText.requestFocus()
        return false
    } else {
        inputlayout.isErrorEnabled = false
    }
    return true
}

/**
 * 1) field must not be empty
 * 2) mobile number validation with 10 digit starting with 6
 */
fun validateMobile(inputlayout: TextInputLayout,editText: TextInputEditText): Boolean {
    if (editText.text.toString().trim().isEmpty()) {
        inputlayout.error = "Required Field!"
        editText.requestFocus()
        return false
    } else if (!editText.text.toString().isPhone()) {
        inputlayout.error = "Enter valid mobile number"
        editText.requestFocus()
        return false
    } else {
        inputlayout.isErrorEnabled = false
    }
    return true
}

/**
 * 1) field must not be empty
 * 2) mobile number validation with 10 digit starting with 6
 */
fun validateEmail(inputlayout: TextInputLayout,editText: TextInputEditText): Boolean {
    if (!editText.text.toString().isEmail()) {
        inputlayout.error = "Enter valid Email ID"
        editText.requestFocus()
        return false
    } else {
        inputlayout.isErrorEnabled = false
    }
    return true
}

/**
 * 1) field must not be empty
 * 2) password lenght must not be less than 6
 */
fun validatePassword(inputlayout: TextInputLayout,editText: TextInputEditText): Boolean {
    if (editText.text.toString().trim().isEmpty()) {
        inputlayout.error = "Required Field!"
        editText.requestFocus()
        return false
    } else if (editText.text.toString().length < 6) {
        inputlayout.error = "password can't be less than 6"
        editText.requestFocus()
        return false
    } else {
        inputlayout.isErrorEnabled = false
    }
    return true
}

/**
 * 1) field must not be empty
 * 2) password and confirm password should be same
 */
fun validateConfirmPassword(inputlayout: TextInputLayout,editText1: TextInputEditText,
                                    editText2: TextInputEditText): Boolean {
        if(editText1.text.toString().trim().isEmpty()){
            inputlayout.error = "Required Field!"
            editText1.requestFocus()
            return false
        }else if (editText1.text.toString().length < 6) {
            inputlayout.error = "password can't be less than 6"
            editText1.requestFocus()
            return false
        }else if(editText1.text.toString() != editText2.text.toString()){
            inputlayout.error = "Passwords don't match"
            editText1.requestFocus()
            return false
        }else {
            inputlayout.isErrorEnabled = false
        }
    return true
}

fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("dd-MM-yyyy")
    return sdf.format(Date())
}
fun appendLog(text: String?) {
    try {
        val logFolder= File(Environment.getExternalStorageDirectory(),"Pagar_Plus")
        val logFile=File(logFolder,"logFile.txt")
        if (!logFolder.exists())
            logFolder.mkdir()
        if(!logFile.exists())
            logFile.createNewFile()
        val buf = BufferedWriter(FileWriter(logFile, true))
        buf.append("${Date()} : $text")
        buf.newLine()
        buf.close()
    } catch (e: IOException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    }
}