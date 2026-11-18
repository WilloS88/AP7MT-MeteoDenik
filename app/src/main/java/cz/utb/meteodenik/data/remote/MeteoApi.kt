package cz.utb.meteodenik.data.remote

import cz.utb.meteodenik.data.remote.dto.GeoOdpovedDto
import cz.utb.meteodenik.data.remote.dto.PredpovedDto
import retrofit2.http.GET
import retrofit2.http.Query

interface MeteoApi {

    @GET("v1/forecast")
    suspend fun aktualniPocasi(
        @Query("latitude") sirka: Double,
        @Query("longitude") delka: Double,
        @Query("current") promenne: String = VYCHOZI_PROMENNE,
        @Query("timezone") casovaZona: String = "auto"
    ): PredpovedDto

    companion object {
        const val VYCHOZI_PROMENNE =
            "temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m"
    }
}

interface GeoApi {

    @GET("v1/search")
    suspend fun najdiMesta(
        @Query("name") nazev: String,
        @Query("count") pocet: Int = 5,
        @Query("language") jazyk: String = "cs",
        @Query("format") format: String = "json"
    ): GeoOdpovedDto
}
