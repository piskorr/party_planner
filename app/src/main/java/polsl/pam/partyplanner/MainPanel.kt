package polsl.pam.partyplanner

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton


class MainPanel : AppCompatActivity() {

    lateinit var buttonProfile: MaterialButton
    lateinit var buttonAddEvent: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_panel)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        buttonProfile = findViewById(R.id.buttonProfile)
        buttonAddEvent = findViewById(R.id.buttonAddEvent)

        buttonProfile.setOnClickListener{
            startActivity(Intent(this, Profile::class.java))
        }

        buttonAddEvent.setOnClickListener{
            startActivity(Intent(this, AddEvent::class.java))
        }
    }
}