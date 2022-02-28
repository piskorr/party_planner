package polsl.pam.partyplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.dto.UserView
import java.lang.Exception


class SignUp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var userViewList: ArrayList<UserView> = ArrayList()
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
        database.child("users")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userViewList.clear()
                    for (postSnapshot in snapshot.children) {
                        val userView: UserView? = postSnapshot.getValue(UserView::class.java)
                        userViewList.add(userView!!)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        baseContext,
                        "Database error: " + databaseError.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

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
        if (validateFields()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT)
                            .show()
                        createUserNode()
                        Log.d(TAG, "createUserWithEmail:success")
                        startActivity(Intent(this, MainPanel::class.java))
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            this,
                            "An error occurred! Check your input: " + task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun validateFields(): Boolean {
        if (textFullName.text.isNullOrBlank() ||
            textUserName.text.isNullOrBlank() ||
            textPassword.text.isNullOrBlank() ||
            textEmail.text.isNullOrBlank()
        ) {
            Toast.makeText(this, "Fill all fields!", Toast.LENGTH_LONG).show()
            return false
        }

        if (textPassword.text.toString().length < 6) {
            Toast.makeText(this, "Password has to have at least 6 characters!", Toast.LENGTH_LONG)
                .show()
            return false
        }

        if (textUserName.text.toString().length < 4) {
            Toast.makeText(this, "username has to have at least 4 characters!", Toast.LENGTH_LONG)
                .show()
            return false
        }

        for (u in userViewList)
            if (u.login == textUserName.text.toString()) {
                Toast.makeText(this, "This login is taken!", Toast.LENGTH_LONG).show()
                return false
            }

        return true
    }

    private fun createUserNode() {
        database
            .child("users")
            .child(auth.currentUser!!.uid)
            .setValue(UserView(textUserName.text.toString(), textFullName.text.toString()))
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}

