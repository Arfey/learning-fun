package elasticsearchplugins.plugins.rest;

import org.elasticsearch.common.settings.Setting;

public class TermListSettingsConfig {
    public static final Setting<Boolean> ENABLE_PLUGIN =
            Setting.boolSetting("index.plugins.termList.enabled", false, Setting.Property.Dynamic, Setting.Property.IndexScope);
}
