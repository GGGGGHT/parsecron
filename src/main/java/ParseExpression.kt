import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.util.castSafelyTo
import org.springframework.scheduling.support.CronExpression
import org.springframework.util.StringUtils
import java.lang.StringBuilder
import java.time.LocalDateTime

class ParseExpression : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val caretModel = editor.caretModel
        val primaryCaret = caretModel.primaryCaret
        val selectedText = primaryCaret.selectedText
        println("selected: $selectedText ")
        if (selectedText == null || selectedText.isBlank()) {
            return
        }


        val validExpression: Any = isValidExpression<Any>(selectedText) ?: return
        var linuxTmp: LinuxCronExpression? = null
        var javaTmp: CronExpression? = null
        if (validExpression is LinuxCronExpression) {
            linuxTmp = validExpression.castSafelyTo<LinuxCronExpression>()!!
        } else {
            javaTmp = validExpression.castSafelyTo<CronExpression>()!!
        }

        val builder: StringBuilder = StringBuilder()
        var next: LocalDateTime? = null;
        for (i in 1..5) {
            if(next == null) {
                next = LocalDateTime.now()
            }
            if (linuxTmp != null) {
                next = linuxTmp.next(next)
                builder.append(next.toString() + "\n")
                continue
            }

            if (javaTmp != null) {
                next = javaTmp.next(next)
                builder.append(next.toString() + "\n")
                continue
            }
        }

        // 展示接下来五次的执行时间
        Messages.showMessageDialog(
            project,
            "Your selected is cron expression: $builder",
            "Greeting",
            Messages.getInformationIcon()
        )
    }


    /**
     *     linux:
     *     *    *    *    *    *
     *     -    -    -    -    -
     *     |    |    |    |    |
     *     |    |    |    |    +----- day of week (0 - 7) (Sunday=0 or 7) OR sun,mon...
     *     |    |    |    +---------- month (1 - 12) OR jan,feb,mar,apr ...
     *     |    |    +--------------- day of month (1 - 31)
     *     |    +-------------------- hour (0 - 23)
     *     +------------------------- minute (0 - 59)
     *
     *    spring:
     *    ┌───────────── second (0-59)
     *    │ ┌───────────── minute (0 - 59)
     *    │ │ ┌───────────── hour (0 - 23)
     *    │ │ │ ┌───────────── day of the month (1 - 31)
     *    │ │ │ │ ┌───────────── month (1 - 12) (or JAN-DEC)
     *    │ │ │ │ │ ┌───────────── day of the week (0 - 7)
     *    │ │ │ │ │ │          (0 or 7 is Sunday, or MON-SUN)
     *    │ │ │ │ │ │
     *    * * * * * *
     *
     *     Java(Quartz)
     *         *    *    *    *    *    *    *
     *         -    -    -    -    -    -    -
     *         |    |    |    |    |    |    |
     *         |    |    |    |    |    |    + year [optional]
     *         |    |    |    |    |    +----- day of week (1 - 7) sun,mon,tue...
     *         |    |    |    |    +---------- month (1 - 12) OR jan,feb,mar,apr ...
     *         |    |    |    +--------------- day of month (1 - 31)
     *         |    |    +-------------------- hour (0 - 23)
     *         |    +------------------------- min (0 - 59)
     *         +------------------------------ second (0 - 59)
     */
    private fun <T> isValidExpression(cron: String): Any? {
        val fields = StringUtils.tokenizeToStringArray(cron, " ")

        if (fields.size < 5 || fields.size > 6) {
            return null
        }

        // linux
        if (fields.size == 5) {
            return LinuxCronExpression.parse(fields)
        }

        // java
        return CronExpression.parse(cron)
    }


}