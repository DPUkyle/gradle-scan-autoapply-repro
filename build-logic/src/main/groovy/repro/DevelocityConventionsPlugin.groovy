package repro

import com.gradle.develocity.agent.gradle.DevelocityPlugin
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

/**
 * Explicitly applies the Develocity plugin to Settings, then would normally configure
 * server URL, publishing rules, etc.
 */
class DevelocityConventionsPlugin implements Plugin<Settings> {
    @Override
    void apply(Settings settings) {
        settings.plugins.apply(DevelocityPlugin)
    }
}
