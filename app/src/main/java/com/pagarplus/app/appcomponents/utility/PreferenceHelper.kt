package com.pagarplus.app.appcomponents.utility

import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.modules.workholidays.ui.WorkholidaysActivity
import com.pagarplus.app.network.models.adminEditEmpdata.AdminEditEmployeeResponse
import com.pagarplus.app.network.models.attendance.FeaturesTypes
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import com.pagarplus.app.network.models.creategetlogindetail.LoginResponse
import kotlin.String
import kotlin.Unit

class PreferenceHelper {
  private val masterKeyAlias: String = createGetMasterKey()


   val sharedPreference: SharedPreferences = EncryptedSharedPreferences.create(
  MyApp.getInstance().resources.getString(R.string.app_name),
  masterKeyAlias,
  MyApp.getInstance().applicationContext,
  EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
  EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
  )
  val es_sharedPreference: SharedPreferences = EncryptedSharedPreferences.create(
    MyApp.getInstance().resources.getString(R.string.app_name),
    masterKeyAlias,
    MyApp.getInstance().applicationContext,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
  )
  val mLogEditor=sharedPreference.edit()
  val mEsDetailsEditor=es_sharedPreference.edit()


  /**
   * Creates or gets the master key provided,
   * The encryption scheme is required fields to ensure that the type of encryption used is clear to
   * developers.
   *
   * @return the string value of encrypted key
   */
  private fun createGetMasterKey(): String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

  fun setAccess_token(paramValue: String?): Unit {
    with(sharedPreference.edit()) {
      this.putString("access_token", paramValue!!)
      apply()
    }
  }

  fun getAccess_token(): String? = sharedPreference.getString("access_token", null)

  fun setLoginTrue(paramValue: Boolean?): Unit {
    with(sharedPreference.edit()) {
      this.putBoolean("isLogin", paramValue!!)
      apply()
    }
  }

  fun getLoginTrue(): Boolean? = sharedPreference.getBoolean("isLogin", false)

  fun setIsAdmin(paramValue: Boolean?): Unit {
    with(sharedPreference.edit()) {
      this.putBoolean("isAdmin", paramValue!!)
      apply()
    }
  }

  fun getIsAdmin(): Boolean? = sharedPreference.getBoolean("isAdmin", false)

  fun <T> setLastLog(`object`: T) {
    val jsonString = GsonBuilder().create().toJson(`object`)
    mLogEditor.putString("AttendanceData", jsonString).apply()
  }


  inline fun <reified T> getLastLog(): T? {
    val value = sharedPreference.getString("AttendanceData", null)
    return GsonBuilder().create().fromJson(value, T::class.java)
  }

  fun <T> setVisitTypes(`object`: T) {
    val jsonString = GsonBuilder().create().toJson(`object`)
    mEsDetailsEditor.putString("Visit", jsonString).apply()
  }

  inline fun <reified T> getVisitTypes(): T? {
    val value = sharedPreference.getString("Visit", null)
    val typeToken = object : TypeToken<ArrayList<FeaturesTypes?>>(){}.type
    return GsonBuilder().create().fromJson(value, typeToken)
  }

  fun <T> setSessionTypes(`object`: T) {
    val jsonString = GsonBuilder().create().toJson(`object`)
    mEsDetailsEditor.putString("Session", jsonString).apply()
  }

  inline fun <reified T> getSessionTypes(): T? {
    val value = sharedPreference.getString("Session", null)
    val typeToken = object : TypeToken<ArrayList<FeaturesTypes?>>(){}.type
    return GsonBuilder().create().fromJson(value, typeToken)
  }

  fun <T> setLoginDetails(`object`: T) {
    val jsonString = GsonBuilder().create().toJson(`object`)
    sharedPreference.edit().putString("LoginDetails", jsonString).apply()
  }
  inline fun <reified T> getLoginDetails(): T? {
    val value = sharedPreference.getString("LoginDetails", null)
    return GsonBuilder().create().fromJson(value, T::class.java)
  }

  fun <T> setProfileDetails(`object`: T) {
    val jsonString = GsonBuilder().create().toJson(`object`)
    sharedPreference.edit().putString("Profile", jsonString).apply()
  }

  inline fun <reified T> getProfileDetails(): T? {
    val value = sharedPreference.getString("Profile", null)
    return GsonBuilder().create().fromJson(value, T::class.java)
  }

  public fun putloginsave(paramValue: Boolean?): Unit {
    Log.e("LoginSave","bool...$paramValue")
    with(sharedPreference.edit()) {
      this.putBoolean("is_logged_in", paramValue!!)
      apply()
    }
  }

  public fun getloginsave(): Boolean=sharedPreference.getBoolean("is_logged_in",false)

  public fun setPassword(paramValue: String?): Unit {
    Log.e("LoginSave","password...$paramValue")
    with(sharedPreference.edit()) {
      this.putString("password", paramValue!!)
      apply()
    }
  }

  public fun getPassword(): String? {
    with(sharedPreference) {
      return getString("password", null)
    }
  }
  public fun setMobile(paramValue: String?): Unit {
    Log.e("LoginSave","mobile...$paramValue")
    with(sharedPreference.edit()) {
      this.putString("mobile_no", paramValue!!)
      apply()
    }
  }

  public fun getMobile(): String? {
    with(sharedPreference) {
      return getString("mobile_no", null)
    }

  }

  /*set and check first time login*/
  fun setFirstLoginTrue(paramValue: Boolean?): Unit {
    with(sharedPreference.edit()) {
      this.putBoolean("isFirstTime", paramValue!!)
      apply()
    }
  }

  fun getFirstLoginTrue(): Boolean? = sharedPreference.getBoolean("isFirstTime", true)

}
