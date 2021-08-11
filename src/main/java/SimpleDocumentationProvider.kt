import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.psi.PsiElement
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class SimpleDocumentationProvider : AbstractDocumentationProvider() {
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        println("element $element")
        println("originalElement $originalElement")
        val string = element?.text?.replace("\"", "")!!
        // timestamp
        if (string.length == 10 || string.length == 13) {
            val time = parseTime(string)
            println(time)


            val sb = StringBuilder()
            sb.append(DocumentationMarkup.DEFINITION_START)
//            sb.append("解析表达式\"$cron \"的结果")
            sb.append(DocumentationMarkup.DEFINITION_END)
            sb.append(DocumentationMarkup.CONTENT_START)
            sb.append(DocumentationMarkup.CONTENT_END)
            sb.append(DocumentationMarkup.SECTIONS_START)
//            addKeyValueSection("表达式类型: ", key, sb)
            addKeyValueSection("时间: ", time, sb)
            sb.append(DocumentationMarkup.SECTIONS_END)
            return sb.toString()
        }
        val fields = StringUtils.tokenizeToStringArray(string, " ")
        if (fields.size < 5 || fields.size > 6) {
            return null;
        }

        val type = if (fields.size == 5) {
            "Linux"
        } else {
            "Java"
        }

        val cron = if (type == "Linux") {
            ParseExpression.isValidExpression("0 $string")
        } else {
            ParseExpression.isValidExpression(string)
        }
        if (cron != null) {
            val next = ParseExpression.getNext(cron)
            println(next)
            return renderFullDoc(type, next, cron)
        }

        return super.generateDoc(element, originalElement)
    }

    private fun renderFullDoc(key: String, docComment: String, cron: Any): String {
        val sb = StringBuilder()
        sb.append(DocumentationMarkup.DEFINITION_START)
        sb.append("解析表达式\"$cron \"的结果")
        sb.append(DocumentationMarkup.DEFINITION_END)
        sb.append(DocumentationMarkup.CONTENT_START)
        sb.append(DocumentationMarkup.CONTENT_END)
        sb.append(DocumentationMarkup.SECTIONS_START)
        addKeyValueSection("表达式类型: ", key, sb)
        addKeyValueSection("最近5次执行时间: ", docComment, sb)
        sb.append(DocumentationMarkup.SECTIONS_END)
        return sb.toString()
    }

    private fun addKeyValueSection(key: String, value: String, sb: StringBuilder) {
        sb.append(DocumentationMarkup.SECTION_HEADER_START)
        sb.append(key)
        sb.append(DocumentationMarkup.SECTION_SEPARATOR)
        sb.append("<p>")
        sb.append(value)
        sb.append(DocumentationMarkup.SECTION_END)
    }

    private fun parseTime(str: String): String {
        var timestamp = java.lang.Long.valueOf(str)
        if (str.length == 13) {
            timestamp /= 1000
        }

        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .format(LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.of("+8"))).toString()
    }
}