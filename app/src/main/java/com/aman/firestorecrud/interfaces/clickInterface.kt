package com.aman.firestorecrud.interfaces

interface clickInterface {
    fun onClick(position: Int, clickType: ClickType ?= ClickType.EDIT)
}

enum class ClickType{
    ADD, EDIT, DELETE
}