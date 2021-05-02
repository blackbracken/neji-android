package black.bracken.neji.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import black.bracken.neji.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userViewModel.signInIfCacheExists()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val navController = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment)
            ?.findNavController()!!
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

}