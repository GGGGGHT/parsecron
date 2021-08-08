import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiLocalVariable
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil

class PsiNavigationDemoAction : AnAction() {
  override fun actionPerformed(e: AnActionEvent) {
    val editor = e.getData(CommonDataKeys.EDITOR)
    val psiFile = e.getData(CommonDataKeys.PSI_FILE)
    if (editor == null || psiFile == null) {
      return
    }

    val offset = editor.caretModel.offset
    val infoBuilder = StringBuilder()
    val element = psiFile.findElementAt(offset)
    infoBuilder.append("lement at caret: $element\n")
    if (element != null) {
      val containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod::class.java)
      infoBuilder
        .append("Containing method: ")
        .append(containingMethod?.name ?: "none")
        .append("\n")
      if (containingMethod != null) {
        val containingClass = containingMethod.containingClass
        infoBuilder
          .append("Containing class: ")
          .append(if (containingClass != null) containingClass.name else "none")
          .append("\n")
        infoBuilder.append("Local variables:\n")
        containingMethod.accept(object : JavaRecursiveElementVisitor() {
          override fun visitLocalVariable(variable: PsiLocalVariable) {
            super.visitLocalVariable(variable)
            infoBuilder.append(variable.name).append("\n")
          }
        })
      }
    }
  }
}