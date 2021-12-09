package com.example

interface AudioCallback {
    fun utterNext()
    fun play(index:Int, utterId:String)
    fun pause(index:Int, utterId:String)
    fun stop(index:Int, utterId:String)
    fun error(index:Int, utterId:String, error:String)
}