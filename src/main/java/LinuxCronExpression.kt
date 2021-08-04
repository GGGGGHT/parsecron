import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal

public data class LinuxCronExpression(
    val minute: CronField,
    val hour: CronField,
    val day: CronField,
    val month: CronField,
    val dayOfWeek: CronField,
    val fields: List<CronField>
) {
    companion object {
        fun parse(cron: Array<String>): LinuxCronExpression {
            val minutes = CronField.parseMinutes(cron[0])
            val hours = CronField.parseHours(cron[1])
            val daysOfMonth = CronField.parseDaysOfMonth(cron[2])
            val months = CronField.parseMonth(cron[3])
            val daysOfWeek = CronField.parseDaysOfWeek(cron[4])

            println("Linux Cron Expression")
            return LinuxCronExpression(
                minutes,
                hours,
                daysOfMonth,
                months,
                daysOfWeek,
                listOf(minutes, hours, daysOfMonth, months, daysOfWeek)
            )

        }

    }

    fun <T> next(temporal: T): T? where T : Temporal?, T : Comparable<T>? {
        return nextOrSame(ChronoUnit.NANOS.addTo(temporal, 1))
    }

    private fun <T> nextOrSame(temporal: T): T? where T : Temporal?, T : Comparable<T>? {
        var temporal = temporal
        for (i in 0 until 366) {
            val result: T = nextOrSameInternal<T>(temporal)
            if (result == null || result == temporal) {
                return result
            }
            temporal = result
        }
        return null
    }

    private fun <T> nextOrSameInternal(temporal: T): T where T : Temporal?, T : Comparable<T>? {
        var temporal: T = temporal
        for (field in fields) {
            temporal = field.nextOrSame(temporal)
            if (temporal == null) {
                return temporal
            }
        }
        return temporal
    }
}