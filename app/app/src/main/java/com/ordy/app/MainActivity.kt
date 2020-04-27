package com.ordy.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.ordy.app.ui.groups.create.CreateGroupActivity
import com.ordy.app.ui.login.LoginActivity
import com.ordy.app.ui.orders.create.CreateOrderActivity
import com.ordy.app.ui.profile.ProfileActivity
import com.ordy.app.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val accessToken = AppPreferences(applicationContext).accessToken

        // Check if the user is logged in.
        // If not, open the login activity
        if (accessToken == null || accessToken.isBlank()) {
            val intent = Intent(this, LoginActivity::class.java)

            // Reset the back stack.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(intent)
            finish()

            return
        }

        // Set the activity layout.
        setContentView(R.layout.activity_main)

        /**
         *  Create FAB speeddial.
         */
        val speedDialView = this.speeddial

        // New order button
        speedDialView
            .addActionItem(
                SpeedDialActionItem.Builder(
                    R.id.speeddial_order,
                    R.drawable.ic_library_books_black_24dp
                )
                    .setLabel(getString(R.string.speeddial_order))
                    .setFabImageTintColor(Color.WHITE)
                    .create()
            )

        // New group button
        speedDialView
            .addActionItem(
                SpeedDialActionItem.Builder(R.id.speeddial_group, R.drawable.ic_group_black_24dp)
                    .setLabel(getString(R.string.speeddial_group))
                    .setFabImageTintColor(Color.WHITE)
                    .create()
            )

        // Click actions.
        speedDialView.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {

                // Create new order.
                R.id.speeddial_order -> {

                    // Open the create order activity
                    val intent = Intent(this, CreateOrderActivity::class.java)
                    startActivity(intent)

                    // Close the speeddial.
                    speedDialView.close()
                }

                // Create new group.
                R.id.speeddial_group -> {

                    // Open the create group activity
                    val intent = Intent(this, CreateGroupActivity::class.java)
                    startActivity(intent)

                    // Close the speeddial.
                    speedDialView.close()
                }
            }
            false
        }

        /**
         * Create bottom navigation.
         */
        val navView = this.nav_view
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_orders,
                R.id.navigation_groups,
                R.id.navigation_payments,
                R.id.navigation_locations
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        /**
         * Create top navigation.
         */
        menuInflater.inflate(R.menu.top_nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.navigation_profile -> {
            // Open the profile activity
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)

            true
        }

        R.id.navigation_settings -> {
            // Open the settings activity
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)

            true
        }

        R.id.navigation_logout -> {
            // Delete the accessToken
            AppPreferences(applicationContext).accessToken = ""

            // Go to the login activity
            val intent = Intent(this, LoginActivity::class.java)

            // Reset the back stack.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(intent)

            finish()

            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
