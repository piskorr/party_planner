package polsl.pam.partyplanner

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import polsl.pam.partyplanner.dto.PartyEvent

import com.google.firebase.database.DatabaseReference

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import polsl.pam.partyplanner.fragments.FriendsFragment
import polsl.pam.partyplanner.fragments.InvitesFragment
import polsl.pam.partyplanner.fragments.PartiesFragment
import polsl.pam.partyplanner.fragments.PlanningFragment

class MainPanel : AppCompatActivity() {

    lateinit var buttonProfile: MaterialButton
    lateinit var database: DatabaseReference
    lateinit var partiesFragment: PartiesFragment
    lateinit var invitesFragment: InvitesFragment
    lateinit var planningFragment: PlanningFragment
    lateinit var friendsFragment: FriendsFragment
    lateinit var bottomNavigation: BottomNavigationView
    var lastFragment: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_panel)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        database = Firebase.database.reference
        partiesFragment = PartiesFragment(database)
        invitesFragment = InvitesFragment(database)
        planningFragment = PlanningFragment(database)
        friendsFragment = FriendsFragment(database)
        setLastFragment()

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    // Respond to navigation item 1 click
                    replaceFragment(partiesFragment)
                    lastFragment = 1
                    true
                }
                R.id.page_2 -> {
                    // Respond to navigation item 2 click
                    replaceFragment(invitesFragment)
                    lastFragment = 2
                    true
                }
                R.id.page_3 -> {
                    // Respond to navigation item 2 click
                    replaceFragment(friendsFragment)
                    lastFragment = 3
                    true
                }
                R.id.page_4 -> {
                    // Respond to navigation item 2 click
                    replaceFragment(planningFragment)
                    lastFragment = 4
                    true
                }
                else -> false
            }
        }

        buttonProfile = findViewById(R.id.buttonProfile)
        buttonProfile.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
        }
    }

    private fun setLastFragment() {
        when (lastFragment) {
            1 -> {
                replaceFragment(partiesFragment)
            }
            2 -> {
                replaceFragment(invitesFragment)
            }
            3 -> {
                replaceFragment(friendsFragment)
            }
            4 -> {
                replaceFragment(planningFragment)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}