package cz.utb.meteodenik.data

import cz.utb.meteodenik.model.Mesto
import cz.utb.meteodenik.model.MeteoZaznam
import cz.utb.meteodenik.model.NalezeneMesto

interface MeteoRepository {

    suspend fun aktualniPocasi(mesto: Mesto, vynutitObnovu: Boolean = false): VysledekSite<MeteoZaznam>

    suspend fun najdiMesta(dotaz: String): VysledekSite<List<NalezeneMesto>>
}
