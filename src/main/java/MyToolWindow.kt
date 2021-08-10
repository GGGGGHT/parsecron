import com.intellij.credentialStore.ProviderType
import com.intellij.diff.impl.DiffSettingsHolder
import com.intellij.diff.tools.util.base.TextDiffSettingsHolder
import com.intellij.execution.console.ConsoleConfigurable
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.layout.panel
import com.intellij.ui.layout.titledRow
import com.intellij.ide.ui.UISettings
import com.intellij.ide.ui.UISettings.Companion.TABS_NONE
import com.intellij.openapi.application.ApplicationBundle.message
import com.intellij.openapi.diff.DiffBundle
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.registry.Registry
import com.intellij.ui.components.JBPanel
import com.intellij.ui.layout.*
//import com.intellij.ui.mac.foundation.ID
import com.intellij.ui.tabs.impl.JBTabsImpl
import com.intellij.ui.tabs.impl.tabsLayout.TabsLayoutInfo
import com.intellij.ui.tabs.layout.TabsLayoutSettingsUi
import org.jetbrains.debugger.getClassName
import javax.swing.*
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener
import kotlin.math.max

class MyToolWindow : BoundSearchableConfigurable(
    DiffBundle.message("configurable.DiffSettingsConfigurable.display.name"),
    "TEST"
) {

    override fun createPanel(): DialogPanel {
        val textSettings = TextDiffSettingsHolder.TextDiffSettings.getSettings()
        val diffSettings = DiffSettingsHolder.DiffSettings.getSettings()

        return panel {
            row{
                label("hello world: ")
            }
        }
    }

}