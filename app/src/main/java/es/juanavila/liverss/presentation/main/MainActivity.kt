package es.juanavila.liverss.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.juanavila.liverss.R
import es.juanavila.liverss.application.usecases.headlines.DeletedAll
import es.juanavila.liverss.application.usecases.headlines.LiveNews
import es.juanavila.liverss.framework.App
import es.juanavila.liverss.presentation.favs.FavsFragment
import es.juanavila.liverss.presentation.headlines.HeadlinesFragment
import es.juanavila.liverss.presentation.settings.SettingsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import javax.inject.Inject


class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var menu : BottomNavigationView
    private lateinit var mainScreenViewModel: MainScreenViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    init {
        App.appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        menu = findViewById(R.id.menuBnv)

        mainScreenViewModel = ViewModelProviders.of(this,viewModelFactory).get(MainScreenViewModel::class.java)

        mainScreenViewModel.scrolledToTop.observe(this, Observer {
            menu.removeBadge(R.id.headlinesMi)
        })

        mainScreenViewModel.appEvents.observe(this, Observer {
            when(it) {
                is LiveNews -> showLiveNewsBadge(it.quantity)
                DeletedAll -> showLiveNewsBadge(0)
            }
        })

        menu.setOnNavigationItemSelectedListener {
            val trans = supportFragmentManager.beginTransaction()

            val curFrag = supportFragmentManager.primaryNavigationFragment

            val originIndex = when (curFrag?.tag) {
                HeadlinesFragment::class.java.simpleName -> 1
                FavsFragment::class.java.simpleName -> 2
                else -> 3
            }

            val destinationIndex = when (it.itemId) {
                R.id.headlinesMi -> {
                    menu.removeBadge(R.id.headlinesMi)
                    1
                }
                R.id.favsMi -> 2
                else -> 3
            }

            if(originIndex < destinationIndex) {
                trans.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left)
            }
            else {
                trans.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
            }

            if (curFrag != null) {
                trans.detach(curFrag)
            }


            val tag = when (it.itemId) {
                R.id.favsMi -> FavsFragment::class.java.simpleName
                R.id.headlinesMi -> HeadlinesFragment::class.java.simpleName
                else -> SettingsFragment::class.java.simpleName
            }

            var fragment = supportFragmentManager.findFragmentByTag(tag)

            if (fragment == null) {
                fragment = when(it.itemId){
                    R.id.favsMi -> FavsFragment.newInstance()
                    R.id.headlinesMi -> HeadlinesFragment.newInstance()
                    else -> SettingsFragment.newInstance()
                }
                trans.add(R.id.container, fragment, tag)
            } else {
                trans.attach(fragment)
            }

            trans.setReorderingAllowed(true)
            trans.setPrimaryNavigationFragment(fragment)
            trans.commit()

            return@setOnNavigationItemSelectedListener true
        }

        if (savedInstanceState == null){
            menu.selectedItemId = R.id.headlinesMi
        }

        menu.setOnNavigationItemReselectedListener {
            val tag : String = when(it.itemId){
                R.id.favsMi -> FavsFragment::class.java.simpleName
                R.id.headlinesMi -> {
                    menu.removeBadge(R.id.headlinesMi)
                    HeadlinesFragment::class.java.simpleName
                }
                else -> SettingsFragment::class.java.simpleName
            }
            mainScreenViewModel.bottomMenuEvents.value =
                Reselected(tag)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val badgeNb = savedInstanceState.getInt("badge_nb",-1)
        if(badgeNb != -1) {
            menu.getOrCreateBadge(R.id.headlinesMi).number = badgeNb
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        menu.getBadge(R.id.headlinesMi)?.let {
            outState.putInt("badge_nb",it.number)
        }
    }

    private fun showLiveNewsBadge(number: Int) {
        if(number == 0) {
            menu.removeBadge(R.id.headlinesMi)
        }
        else {
            val badge: BadgeDrawable = menu.getOrCreateBadge(R.id.headlinesMi)
            badge.number = number
        }
    }


    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag(HeadlinesFragment::class.java.simpleName)!!
        if(fragment.isVisible) {
            MaterialAlertDialogBuilder(this)
                .setTitle("¿Quieres salir?")
                .setNegativeButton("Salir") { _, _ -> super.onBackPressed() }
                .setPositiveButton("Volver a la app",null)
                .show()
        }
        else {
            menu.selectedItemId = R.id.headlinesMi
        }
    }
}
