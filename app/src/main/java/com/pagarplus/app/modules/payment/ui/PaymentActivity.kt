package com.pagarplus.app.modules.payment.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.base.BaseActivity
import com.pagarplus.app.databinding.ActivityPaymentBinding
import com.pagarplus.app.modules.payment.data.viewmodel.PaymentVM
import kotlin.String
import kotlin.Unit

class PaymentActivity : BaseActivity<ActivityPaymentBinding>(R.layout.activity_payment) {
  private val viewModel: PaymentVM by viewModels<PaymentVM>()

  override fun onInitialized(): Unit {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    binding.paymentVM = viewModel
  }

  override fun setUpClicks(): Unit {
    binding.btnAccount.setOnClickListener{
      binding.btnAccount.setBackgroundResource(R.drawable.rectangle_bg_indigo_a700_border_white_a700_radius_tr_20_br_20)
      binding.btnUpi.setBackgroundResource(R.drawable.rectangle_bg_light_blue_a700_border_white_a700_radius_tl_20_bl_20)
      binding.LinearBank.isVisible = true
      binding.LinearUPI.isVisible = false
    }
    binding.btnUpi.setOnClickListener{
      binding.btnAccount.setBackgroundResource(R.drawable.rectangle_bg_light_blue_a700_border_white_a700_radius_tr_20_br_20)
      binding.btnUpi.setBackgroundResource(R.drawable.rectangle_bg_indigo_a701_border_white_a700_radius_tl_20_bl_20)
      binding.LinearBank.isVisible = false
      binding.LinearUPI.isVisible = true
    }
  }

  companion object {
    const val TAG: String = "PAYMENT_ACTIVITY"

    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, PaymentActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }
}
