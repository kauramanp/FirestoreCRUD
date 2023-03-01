package com.aman.firestorecrud

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.aman.firestorecrud.databinding.ActivityMainBinding
import com.aman.firestorecrud.databinding.LayoutAddUpdateBinding
import com.aman.firestorecrud.interfaces.ClickInterface
import com.aman.firestorecrud.interfaces.ClickType
import com.aman.firestorecrud.models.UserModel
import com.aman.firestorecrud.recyclers.UserAdapter
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    var userModelList = arrayListOf<UserModel>()
    lateinit var adapter: UserAdapter
    val db = Firebase.firestore
    lateinit var layoutManager: LayoutManager
    private  val TAG = MainActivity::class.java.canonicalName
    var collectionName = "Users"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initialising binding for the view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialisng adapter and click listener
        adapter = UserAdapter(userModelList, object : ClickInterface{
            override fun onClick(position: Int, clickType: ClickType?) : Boolean {
                when(clickType){
                    ClickType.EDIT -> showDialogFun(position)
                    ClickType.DELETE-> {
                        AlertDialog.Builder(this@MainActivity).apply {
                            setTitle(resources.getString(R.string.delete_alert))
                            //name from the model is passed to string.xml file key to show there with text
                            setTitle(resources.getString(R.string.delete_message, userModelList[position].name.toString()))
                            setPositiveButton(resources.getString(R.string.yes)){_,_->
                                //deleting the particular collection from firestore
                                db.collection(collectionName).document(userModelList[position].id?:"").delete()

                            }
                            setNegativeButton(resources.getString(R.string.no)){_,_->}
                            show()
                        }
                    }
                    else->{}
                }
                return true
            }
        })

        layoutManager = LinearLayoutManager(this)
        binding.recycler.layoutManager = layoutManager
        binding.recycler.adapter = adapter

        db.collection(collectionName
)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                for (snapshot in snapshots!!.documentChanges) {
                    when (snapshot.type) {
                        DocumentChange.Type.ADDED -> {
                            val userModel:UserModel?= snapshot.document.toObject(UserModel::class.java)
                            userModel?.id = snapshot.document.id?:""
                            userModel?.let { userModelList.add(it) }
                            Log.e(TAG,"userModelList ${userModelList.size}")
                            adapter.notifyDataSetChanged()
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val userModel:UserModel?= snapshot.document.toObject(UserModel::class.java)
                            userModel?.id = snapshot.document.id?:""
                            userModel?.let {
                                var index = -1
                                index = userModelList.indexOfFirst { element-> element.id == it.id }
                                if(index>-1)
                                    userModelList.set(index, it)
                            }
                            adapter.notifyDataSetChanged()


                        }
                        DocumentChange.Type.REMOVED -> {
                            val userModel:UserModel?= snapshot.document.toObject(UserModel::class.java)
                            userModel?.let {
                                var index = -1
                                index = userModelList.indexOfFirst { element-> element.id == it.id }
                                if(index>-1)
                                    userModelList.removeAt(index)
                            }
                            adapter.notifyDataSetChanged()

                        }
                    }
                }
            }
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

    fun showDialogFun(position: Int = -1){
        var dialogBinding = LayoutAddUpdateBinding.inflate(layoutInflater)
       var dialog = Dialog(this).apply {
            setContentView(dialogBinding.root)
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            show()
        }
        dialogBinding.etAddress.doOnTextChanged { text, _,_,_ ->
            var textLength = text?.length?:0
            if(textLength>0){
                dialogBinding.tilAddress.isErrorEnabled = false
            }else{
                dialogBinding.tilAddress.isErrorEnabled = true
                dialogBinding.tilAddress.error = resources.getString(R.string.enter_address)
            }
        }
        dialogBinding.etName.doOnTextChanged { text, _,_,_ ->
            var textLength = text?.length?:0
            if(textLength>0){
                dialogBinding.tilName.isErrorEnabled = false
            }else{
                dialogBinding.tilName.isErrorEnabled = true
                dialogBinding.tilName.error = resources.getString(R.string.enter_name)
            }
        }

        dialogBinding.position = position
        if(position>-1){
            dialogBinding.userModel = userModelList[position]
            dialogBinding.btnClick.setText(resources.getString(R.string.add))
        }else{
            dialogBinding.userModel = UserModel()
            dialogBinding.btnClick.setText(resources.getString(R.string.update))

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
                System.out.println("position $position")
                if(position>-1){
                    userModelList[position].name =  dialogBinding.etName.text.toString()
                    userModelList[position].address =  dialogBinding.etAddress.text.toString()
                    db.collection(collectionName).document(userModelList[position].id?:"").set(UserModel( dialogBinding.etName.text.toString(), dialogBinding.etAddress.text.toString(), userModelList[position].id?:""))

                    dialog.dismiss()

                }else{
                    db.collection(collectionName).add(UserModel( dialogBinding.etName.text.toString(), dialogBinding.etAddress.text.toString()))
                    dialog.dismiss()
                }
            }
        }
    }
}