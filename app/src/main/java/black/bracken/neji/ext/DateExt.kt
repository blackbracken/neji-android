package black.bracken.neji.ext

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun Date.toLocalTimeDate(): LocalDateTime =
    LocalDateTime.ofInstant(toInstant(), ZoneId.systemDefault())