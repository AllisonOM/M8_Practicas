package com.example.apppractica2.data.remote.model

import com.google.gson.annotations.SerializedName

data class AutoDto(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("brand")
    var brand: String? = null,
    @SerializedName("model")
    var model: String? = null
)