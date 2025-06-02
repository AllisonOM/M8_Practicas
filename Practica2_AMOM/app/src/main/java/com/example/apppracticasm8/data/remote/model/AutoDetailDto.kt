package com.example.apppractica2.data.remote.model

import com.google.gson.annotations.SerializedName

data class AutoDetailDto(
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("brand")
    var brand: String? = null,
    @SerializedName("model")
    var model: String? = null,
    @SerializedName("version")
    var version: String? = null,
    @SerializedName("power")
    var power: String? = null,
    @SerializedName("acceleration")
    var acceleration: String? = null,
    @SerializedName("top_speed")
    var topSpeed: String? = null,
    @SerializedName("drivetrain")
    var drivetrain: String? = null,
    @SerializedName("engine")
    var engine: String? = null
)