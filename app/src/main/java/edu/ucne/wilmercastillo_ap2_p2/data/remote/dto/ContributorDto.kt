package edu.ucne.wilmercastillo_ap2_p2.data.remote.dto

import com.squareup.moshi.Json

data class ContributorDto (
    @Json(name = "login") val login: String,
    @Json(name = "id") val id: Int?,
    @Json(name = "contributions") val contributions:Int
)