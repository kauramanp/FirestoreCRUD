package com.aman.firestorecrud.interfaces

interface ClickInterface {
    fun onClick(position: Int, clickType: ClickType ?= ClickType.EDIT) :Boolean
}

enum class ClickType{
    ADD, EDIT, DELETE
}