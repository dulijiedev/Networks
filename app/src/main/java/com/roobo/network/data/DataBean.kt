package com.roobo.network.data

import com.dol.networklib.data.Optional

data class MusicModel(
    val name: String,
    val imageUrl: String
)

class ApiResponse<T> {
    var errorCode: Int = 0

    var errorMsg: String = ""

    var data: T? = null


    override fun toString(): String {
        return "ApiResponse(errorCode:'$errorCode',errorMsg:'$errorMsg', data:'$data')"
    }

    fun transform(): Optional<T> {
        return Optional(data)
    }
}

data class BannerDataItem(
    val desc: String,
    val id: Int,
    val imagePath: String,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String
)