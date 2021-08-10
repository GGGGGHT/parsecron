import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.util.castSafelyTo
import org.springframework.scheduling.support.CronExpression
import java.lang.StringBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ParseExpression : AnAction() {
    companion object Factory {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        fun create(): ParseExpression = ParseExpression()

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
        fun isValidExpression(cron: String): Any? {
            return CronExpression.parse(cron)
        }

        fun getNext(validExpression: Any): String {
            val javaTmp = validExpression.castSafelyTo<CronExpression>()!!
            val builder = StringBuilder()
            var next: LocalDateTime? = null
            for (i in 1..5) {
                if (next == null) {
                    next = LocalDateTime.now()
                }

                println("当前第$i 次的next为: $next")

                next = javaTmp.next(next)
                builder.append(dateFormatter.format(next) + "\n")
            }

            return builder.toString()
        }
    }

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


        val validExpression: Any = isValidExpression(selectedText) ?: return
        val builder = getNext(validExpression)

        // 展示接下来五次的执行时间
        Messages.showMessageDialog(
            project,
            "Your selected is cron expression: $builder",
            "Greeting",
            Messages.getInformationIcon()
        )
    }


}