package cz.utb.meteodenik.data

import cz.utb.meteodenik.data.remote.GeoApi
import cz.utb.meteodenik.data.remote.MeteoApi
import cz.utb.meteodenik.data.remote.Sit
import cz.utb.meteodenik.data.remote.toDomain
import cz.utb.meteodenik.model.Mesto
import cz.utb.meteodenik.model.MeteoZaznam
import cz.utb.meteodenik.model.NalezeneMesto
import java.util.concurrent.ConcurrentHashMap

class MeteoRepositoryImpl(
    private val meteoApi: MeteoApi = Sit.meteoApi,
    private val geoApi: GeoApi = Sit.geoApi
) : MeteoRepository {

    private data class ZaznamVCache(val zaznam: MeteoZaznam, val stazenoV: Long)

    private val cache = ConcurrentHashMap<Int, ZaznamVCache>()

    override suspend fun aktualniPocasi(
        mesto: Mesto,
        vynutitObnovu: Boolean
    ): VysledekSite<MeteoZaznam> {
        val ulozene = cache[mesto.id]
        val jeSvezi = ulozene != null &&
            System.currentTimeMillis() - ulozene.stazenoV < PLATNOST_CACHE_MS
        if (!vynutitObnovu && ulozene != null && jeSvezi) {
            return VysledekSite.Uspech(ulozene.zaznam)
        }

        val vysledek = bezpecneVolani {
            meteoApi.aktualniPocasi(sirka = mesto.sirka, delka = mesto.delka).toDomain()
        }
        if (vysledek is VysledekSite.Uspech) {
            cache[mesto.id] = ZaznamVCache(vysledek.data, System.currentTimeMillis())
        }
        return vysledek
    }

    override suspend fun najdiMesta(dotaz: String): VysledekSite<List<NalezeneMesto>> =
        bezpecneVolani {
            geoApi.najdiMesta(nazev = dotaz).results.map { it.toDomain() }
        }

    private companion object {
        const val PLATNOST_CACHE_MS = 10 * 60 * 1000L
    }
}

object Repozitare {
    val meteo: MeteoRepository by lazy { MeteoRepositoryImpl() }
}
