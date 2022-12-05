import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.modules.userlogin.ui.UserloginActivity
import com.pagarplus.app.network.models.createcreateemployee.CreateCreateEmployeeRequest
import org.koin.core.KoinComponent
import org.koin.core.inject

class SplashScreenActivity: AppCompatActivity(),KoinComponent {
    val pref: PreferenceHelper by inject()
    val profile=pref.getProfileDetails<CreateCreateEmployeeRequest>()
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splashscreen)

    // This is used to hide the status bar and make
    // the splash screen as a full screen activity.
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

    // we used the postDelayed(Runnable, time) method
    // to send a message with a delayed time.
    Handler(Looper.getMainLooper()).postDelayed({
        var token=pref.getAccess_token()
        if(token!=null){


        }else{
            val intent=UserloginActivity.getIntent(this,null)
            startActivity(intent)
            finish()
        }
      this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
    }, 3000)// 3000 is the delayed time in milliseconds.
  }
}