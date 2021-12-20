package polsl.pam.partyplanner

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.dto.User


class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    lateinit var buttonSignUp: Button
    lateinit var buttonBack: Button
    lateinit var textFullName: TextInputEditText
    lateinit var textUserName: TextInputEditText
    lateinit var textPassword: TextInputEditText
    lateinit var textEmail: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        auth = Firebase.auth
        database = Firebase.database.reference

        textFullName = findViewById(R.id.fullname)
        textUserName = findViewById(R.id.username)
        textPassword = findViewById(R.id.password)
        textEmail = findViewById(R.id.email)
        buttonSignUp = findViewById(R.id.buttonSignUp)
        buttonBack = findViewById(R.id.buttonBack)

        buttonBack.setOnClickListener {
            val loginIntent = Intent(this, Login::class.java)
            startActivity(loginIntent)
        }

        buttonSignUp.setOnClickListener {
            createAccount(textEmail.text.toString(), textPassword.text.toString())
        }

    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                    createUserNode()
                    Log.d(TAG, "createUserWithEmail:success")
                    startActivity(Intent(this, MainPanel::class.java))
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createUserNode(){
        database.child("users").child(auth.currentUser!!.uid).setValue(User(textUserName.text.toString(), textFullName.text.toString()))
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}

