package cz.utb.meteodenik.rozbite

// ZÁMĚRNĚ ROZBITÝ KÓD — šest chyb, každá jiného druhu.

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.SerialName
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

data class RozbitePredpovedDto(
    val latitude: Double,
    @SerialName("current_units") val jednotky: RozbiteJednotkyDto,
    val current: RozbiteAktualneDto
)

data class RozbiteJednotkyDto(
    @SerialName("temperature_2m") val teplota: String
)

data class RozbiteAktualneDto(
    val time: String,
    @SerialName("temperature_2m") val teplotaC: Double
)

interface RozbitaApi {
    @GET("v1/forecast")
    fun aktualniPocasi(
        @Query("latitude") sirka: Double,
        @Query("longitude") delka: Double,
        @Query("current") promenne: String = "temperature_2m"
    ): RozbitePredpovedDto
}

object RozbitaSit {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://api.open-meteo.com/")
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()

    val api: RozbitaApi = retrofit.create(RozbitaApi::class.java)
}

class RozbityViewModel : ViewModel() {

    val teplota = MutableStateFlow<Double?>(null)

    fun nacti() {
        try {
            val odpoved = RozbitaSit.api.aktualniPocasi(49.2265, 17.6683)
            teplota.value = odpoved.current.teplotaC
        } catch (e: Exception) {
            teplota.value = null
        }
    }
}
