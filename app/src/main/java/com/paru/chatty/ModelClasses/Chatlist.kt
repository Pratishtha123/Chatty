package com.paru.chatty.ModelClasses

class Chatlist {
    private var id:String?=""

    constructor()

    constructor(id: String?) {
        this.id = id
    }

    fun getID():String?{
        return  id
    }

    fun setId(url:String?)
    {
        this.id=id!!
    }

}