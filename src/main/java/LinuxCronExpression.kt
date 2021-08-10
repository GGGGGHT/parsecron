import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal

public data class LinuxCronExpression(
    val minute: CronField,
    val hour: CronField,
    val day: CronField,
    val month: CronField,
    val dayOfWeek: CronField,
    val fields: List<CronField>,
    val expression: String
) {
    companion object {
        fun parse(cron: Array<String>): LinuxCronExpression {
            val minutes = CronField.parseMinutes(cron[0])
            val hours = CronField.parseHours(cron[1])
            val daysOfMonth = CronField.parseDaysOfMonth(cron[2])
            val months = CronField.parseMonth(cron[3])
            val daysOfWeek = CronField.parseDaysOfWeek(cron[4])

            return LinuxCronExpression(
                minutes,
                hours,
                daysOfMonth,
                months,
                daysOfWeek,
                listOf(daysOfWeek, months, daysOfMonth, hours, minutes, CronField.zeroNanos()),
                cron.joinToString(" ")
            )

        }

    }

    val MAX_ATTEMPTS = 366
    override fun toString(): String {
        return this.expression
    }

    fun <T> next(temporal: T): T? where T : Temporal?, T : Comparable<T>? {
        return nextOrSame(ChronoUnit.NANOS.addTo(temporal, 1))
    }

    private fun <T> nextOrSame(temporal: T): T? where T : Temporal?, T : Comparable<T>? {
        var temporal = temporal
        for (i in 0 until MAX_ATTEMPTS) {
            val result: T? = nextOrSameInternal(temporal)
            if (result == null || result == temporal) {
                return result
            }
            temporal = result
        }
        return null
    }

    private fun <T> nextOrSameInternal(temporal: T): T? where T : Temporal?, T : Comparable<T>? {
        var temporal: T = temporal
        for (field in fields) {
            temporal = field.nextOrSame(temporal)
            if (temporal == null) {
                return null
            }
        }
        return temporal
    }
}