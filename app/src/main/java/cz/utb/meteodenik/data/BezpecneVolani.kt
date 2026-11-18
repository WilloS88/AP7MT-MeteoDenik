package cz.utb.meteodenik.data

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.format.DateTimeParseException
import kotlin.coroutines.cancellation.CancellationException

internal suspend fun <T> bezpecneVolani(blok: suspend () -> T): VysledekSite<T> =
    try {
        VysledekSite.Uspech(blok())
    } catch (e: CancellationException) {
        throw e
    } catch (e: UnknownHostException) {
        VysledekSite.Selhani(DuvodSelhani.BEZ_PRIPOJENI)
    } catch (e: ConnectException) {
        VysledekSite.Selhani(DuvodSelhani.BEZ_PRIPOJENI)
    } catch (e: SocketTimeoutException) {
        VysledekSite.Selhani(DuvodSelhani.SERVER_NEODPOVIDA)
    } catch (e: HttpException) {
        VysledekSite.Selhani(
            when {
                e.code() == 429 -> DuvodSelhani.PRILIS_MNOHO_DOTAZU
                e.code() in 400..499 -> DuvodSelhani.CHYBA_POZADAVKU
                else -> DuvodSelhani.CHYBA_SERVERU
            }
        )
    } catch (e: SerializationException) {
        VysledekSite.Selhani(DuvodSelhani.NEPLATNA_ODPOVED)
    } catch (e: DateTimeParseException) {
        VysledekSite.Selhani(DuvodSelhani.NEPLATNA_ODPOVED)
    } catch (e: IOException) {
        VysledekSite.Selhani(DuvodSelhani.JINA_CHYBA_SITE)
    }
