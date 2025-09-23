package com.plcoding.cmpmastermeme

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform