package si.um.feri.mobilegarden.models

data class ExtremeEvent(
    val id: String,
    val time: Long,
    val type: String,
    val level: Int,
    val description: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double
)