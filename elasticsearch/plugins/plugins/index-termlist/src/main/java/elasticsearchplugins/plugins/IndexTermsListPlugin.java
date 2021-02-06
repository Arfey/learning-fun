package elasticsearchplugins.plugins;

import elasticsearchplugins.plugins.rest.TermListRestHandler;
import elasticsearchplugins.plugins.rest.TermListSettingsConfig;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class IndexTermsListPlugin extends Plugin implements ActionPlugin {
    private final Settings settings;

    public IndexTermsListPlugin(Settings settings) {
        this.settings = settings;
    }

    @Override
    public List<ActionHandler<? extends ActionRequest, ? extends ActionResponse>> getActions() {
        // todo: why does we need actions?

        return Collections.emptyList();
    }

    @Override
    public List<Setting<?>> getSettings() {
        return Arrays.asList(
            TermListSettingsConfig.ENABLE_PLUGIN
        );
    }

    @Override
    public List<RestHandler> getRestHandlers(
            Settings settings,
            RestController restController,
            ClusterSettings clusterSettings,
            IndexScopedSettings indexScopedSettings,
            SettingsFilter settingsFilter,
            IndexNameExpressionResolver indexNameExpressionResolver,
            Supplier<DiscoveryNodes> nodesInCluster
    ) {
        Boolean pluginIsEnable = settings.getAsBoolean("plugins.termList.enabled", true);

        if (pluginIsEnable) {
            return Collections.singletonList(new TermListRestHandler(restController));
        }

        return Collections.emptyList();
    }
}
