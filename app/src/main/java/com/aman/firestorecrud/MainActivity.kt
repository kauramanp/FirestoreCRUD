package com.aman.firestorecrud

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.aman.firestorecrud.databinding.ActivityMainBinding
import com.aman.firestorecrud.databinding.LayoutAddUpdateBinding
import com.aman.firestorecrud.interfaces.ClickType
import com.aman.firestorecrud.interfaces.clickInterface
import com.aman.firestorecrud.models.UserModel
import com.aman.firestorecrud.recyclers.UserAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    var userModelList = arrayListOf<UserModel>()
    lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        adapter = UserAdapter(userModelList, object : clickInterface{
            override fun onClick(position: Int, clickType: ClickType?) {
                showDialogFun(position)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                showDialogFun()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showDialogFun(position: Int?= 0){
        var dialogBinding = LayoutAddUpdateBinding.inflate(layoutInflater)
        Dialog(this).apply {
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setContentView(dialogBinding.root)
            show()
        }
        dialogBinding.etAddress.doOnTextChanged { text, start, before, count ->
            var textLength = text?.length?:0
            if(textLength>0){
                dialogBinding.tilAddress.isErrorEnabled = false
            }else{
                dialogBinding.tilAddress.isErrorEnabled = true
                dialogBinding.tilAddress.error = resources.getString(R.string.enter_address)
            }
        }
        dialogBinding.etName.doOnTextChanged { text, start, before, count ->
            var textLength = text?.length?:0
            if(textLength>0){
                dialogBinding.tilName.isErrorEnabled = false
            }else{
                dialogBinding.tilName.isErrorEnabled = true
                dialogBinding.tilName.error = resources.getString(R.string.enter_name)
            }
        }

        dialogBinding.position = position
        if((position?:0)>-1){
            dialogBinding.userModel = userModelList[position?:0]
        }else{
            dialogBinding.userModel = UserModel()
        }

        dialogBinding.btnClick.setOnClickListener {
            if(dialogBinding.etName.text.toString().isNullOrEmpty()){
                dialogBinding.tilName.isErrorEnabled = true
                dialogBinding.tilName.error = resources.getString(R.string.enter_name)
            }else  if(dialogBinding.etAddress.text.toString().isNullOrEmpty()){
                dialogBinding.tilAddress.isErrorEnabled = true
                dialogBinding.tilAddress.error = resources.getString(R.string.enter_address)
            }else{
                //add in firebase
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}