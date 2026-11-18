package cz.utb.meteodenik.rozbite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.utb.meteodenik.data.DuvodSelhani
import cz.utb.meteodenik.data.VysledekSite
import cz.utb.meteodenik.data.bezpecneVolani
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

@Serializable
data class RozbitePredpovedDto(
    val latitude: Double,
    @SerialName("current_units") val jednotky: RozbiteJednotkyDto,
    val current: RozbiteAktualneDto
)

@Serializable
data class RozbiteJednotkyDto(
    @SerialName("temperature_2m") val teplota: String
)

@Serializable
data class RozbiteAktualneDto(
    val time: String,
    @SerialName("temperature_2m") val teplotaC: Double
)

interface RozbitaApi {
    @GET("v1/forecast")
    suspend fun aktualniPocasi(
        @Query("latitude") sirka: Double,
        @Query("longitude") delka: Double,
        @Query("current") promenne: String = "temperature_2m"
    ): RozbitePredpovedDto
}

object RozbitaSit {
    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val api: RozbitaApi = retrofit.create(RozbitaApi::class.java)
}

sealed interface RozbityStav {
    data object Nacitam : RozbityStav
    data class Hotovo(val teplotaC: Double) : RozbityStav
    data class Chyba(val duvod: DuvodSelhani) : RozbityStav
}

class RozbityViewModel : ViewModel() {

    private val _stav = MutableStateFlow<RozbityStav>(RozbityStav.Nacitam)
    val stav: StateFlow<RozbityStav> = _stav.asStateFlow()

    init {
        nacti()
    }

    fun nacti() {
        viewModelScope.launch {
            _stav.value = RozbityStav.Nacitam
            _stav.value = when (val vysledek = bezpecneVolani {
                RozbitaSit.api.aktualniPocasi(49.2265, 17.6683)
            }) {
                is VysledekSite.Uspech -> RozbityStav.Hotovo(vysledek.data.current.teplotaC)
                is VysledekSite.Selhani -> RozbityStav.Chyba(vysledek.duvod)
            }
        }
    }
}
