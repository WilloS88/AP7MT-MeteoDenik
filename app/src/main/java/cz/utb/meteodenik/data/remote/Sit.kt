package cz.utb.meteodenik.data.remote

import cz.utb.meteodenik.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object Sit {

    private const val URL_PREDPOVED = "https://api.open-meteo.com/"
    private const val URL_GEOKODOVANI = "https://geocoding-api.open-meteo.com/"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val klient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
            }
        }
        .build()

    private fun retrofit(zakladniUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(zakladniUrl)
        .client(klient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val meteoApi: MeteoApi by lazy { retrofit(URL_PREDPOVED).create(MeteoApi::class.java) }
    val geoApi: GeoApi by lazy { retrofit(URL_GEOKODOVANI).create(GeoApi::class.java) }
}
