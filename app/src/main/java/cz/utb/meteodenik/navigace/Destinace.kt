package cz.utb.meteodenik.navigace

import kotlinx.serialization.Serializable

@Serializable
data object Seznam

@Serializable
data class Detail(val mestoId: Int)
