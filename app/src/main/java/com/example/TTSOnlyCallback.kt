package com.example

interface TTSOnlyCallback {
    fun initialised()
    fun error(error : String ?= null)
    fun done(done : String ?= null)
    fun start(start : String ?= null)
}