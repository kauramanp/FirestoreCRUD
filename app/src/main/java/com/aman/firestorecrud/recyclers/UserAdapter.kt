package com.aman.firestorecrud.recyclers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aman.firestorecrud.databinding.ContentMainBinding
import com.aman.firestorecrud.databinding.LayoutRecyclerBinding
import com.aman.firestorecrud.interfaces.clickInterface
import com.aman.firestorecrud.models.UserModel

class UserAdapter(var arrayList: ArrayList<UserModel>, var clickListener: clickInterface)  : RecyclerView.Adapter<UserAdapter.ViewHolder>(){
    class ViewHolder(var binding: LayoutRecyclerBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindData(userModel: UserModel,position: Int, clickListener: clickInterface){
            binding.userModel = userModel
            binding.position = position
            binding.clickListener = clickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutRecyclerBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(arrayList[position], position, clickListener)

    }

    override fun getItemCount(): Int =arrayList.size
}