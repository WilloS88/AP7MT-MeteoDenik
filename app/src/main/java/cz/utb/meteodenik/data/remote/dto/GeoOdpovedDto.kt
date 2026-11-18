package cz.utb.meteodenik.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeoOdpovedDto(
    val results: List<GeoMistoDto> = emptyList()
)

@Serializable
data class GeoMistoDto(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    val admin1: String? = null,
    val timezone: String? = null
)
