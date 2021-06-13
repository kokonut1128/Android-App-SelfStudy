package com.bu.selfstudy

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.bu.selfstudy.databinding.ActivityMainBinding
import com.bu.selfstudy.tool.putBundle
import com.bu.selfstudy.tool.showSnackbar
import com.bu.selfstudy.tool.showToast
import com.bu.selfstudy.tool.viewBinding
import com.bu.selfstudy.ui.book.BookFragmentDirections
import com.bu.selfstudy.ui.wordcard.WordCardFragmentDirections
import kotlinx.coroutines.delay


class MainActivity : AppCompatActivity(){
    private val activityViewModel: ActivityViewModel by viewModels()

    private val binding : ActivityMainBinding by viewBinding()

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)

        setSupportActionBar(binding.toolbar)

        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navView.setupWithNavController(navController)

        activityViewModel.memberLiveData.observe(this){
            binding.navView.findViewById<TextView>(R.id.mailField)?.setText(it.email)
            binding.navView.findViewById<TextView>(R.id.userNameField)?.setText(it.userName)
        }

        activityViewModel.bookListLiveData.observe(this){
            activityViewModel.refreshBookIdList(it)
        }

        activityViewModel.databaseEvent.observe(this){
            when(it?.first){
                "insertWord"-> binding.root.showSnackbar("新增成功", "檢視"){
                    val wordId = it.second!!.getLong("wordId")!!
                    val bookId = it.second!!.getLong("bookId")!!

                    activityViewModel.currentOpenBookLiveData.value =
                        activityViewModel.bookListLiveData.value!!.find { it.id == bookId }

                    navController.currentDestination?.let {
                        val action = if(it.id == R.id.wordCardFragment)
                            WordCardFragmentDirections.actionWordCardFragmentSelf(wordId)
                        else
                            BookFragmentDirections.actionBookFragmentToWordCardFragment(wordId)

                        navController.navigate(action)
                    }
                }
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController)||
                super.onOptionsItemSelected(item)
    }

}