package elasticsearchplugins.plugins;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.support.GroupedActionListener;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.Table;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestResponse;
import org.elasticsearch.rest.action.RestResponseListener;
import org.elasticsearch.rest.action.cat.AbstractCatAction;
import org.elasticsearch.rest.action.cat.RestTable;

import java.util.Collection;

import static org.elasticsearch.rest.RestRequest.Method.GET;

public class RestIndicesActionHandler extends AbstractCatAction {
    private static String URL_PATH = "/_cat/custom_indices";
    private static String URL_PATH_WITH_INDEX =  String.format("%s/{index}", URL_PATH);

    public RestIndicesActionHandler(RestController controller) {
        controller.registerHandler(GET, URL_PATH, this);
        controller.registerHandler(GET, URL_PATH_WITH_INDEX, this);
    }

    @Override
    protected RestChannelConsumer doCatRequest(RestRequest request, NodeClient client) {
        final String[] indices = Strings.splitStringByCommaToArray(request.param("index"));
        return channel -> {
            ActionListener<Table> listener = ActionListener.notifyOnce(new RestResponseListener<Table>(channel) {
                @Override
                public RestResponse buildResponse(Table table) throws Exception {
                    return RestTable.buildResponse(table, channel);
                }
            });

            GroupedActionListener<ActionResponse> group = new GroupedActionListener<>(new ActionListener<Collection<ActionResponse>>() {
                @Override
                public void onResponse(Collection<ActionResponse> actionResponses) {
                    ClusterStateResponse state = (ClusterStateResponse) actionResponses
                            .stream()
                            .filter(ClusterStateResponse.class::isInstance)
                            .findFirst()
                            .get();

                    IndicesStatsResponse stats = (IndicesStatsResponse) actionResponses
                            .stream()
                            .filter(IndicesStatsResponse.class::isInstance)
                            .findFirst()
                            .get();

                    ClusterHealthResponse health = (ClusterHealthResponse) actionResponses
                            .stream()
                            .filter(ClusterHealthResponse.class::isInstance)
                            .findFirst()
                            .get();

                    GetSettingsResponse settings = (GetSettingsResponse) actionResponses
                            .stream()
                            .filter(GetSettingsResponse.class::isInstance)
                            .findFirst()
                            .get();

                    Table table = getTableWithHeader(request);

                    for (ObjectObjectCursor<String, Settings> key: settings.getIndexToSettings()) {
                        table.startRow();
                        table.addCell(key.key);
                        table.addCell(stats.getIndex(key.key).getTotal().getDocs().getCount());
                        table.endRow();
                    }

                    listener.onResponse(table);
                }

                @Override
                public void onFailure(Exception e) {
                    listener.onFailure(e);
                }
            }, 4);

            // get settings to iterate throw indices for handle access to indices for difference items
            getIndicesSettings(indices, client, new ActionListener<GetSettingsResponse>() {
                @Override
                public void onResponse(GetSettingsResponse getSettingsResponse) {
                    group.onResponse(getSettingsResponse);
                    getClusterState(indices, client, ActionListener.wrap(group::onResponse, group::onFailure));
                    getIndicesStats(indices, client, ActionListener.wrap(group::onResponse, group::onFailure));
                    getClusterHealth(indices, client, ActionListener.wrap(group::onResponse, group::onFailure));
                }

                @Override
                public void onFailure(Exception e) {
                    listener.onFailure(e);
                }
            });
        };
    }

    @Override
    protected void documentation(StringBuilder sb) {
        sb.append(URL_PATH);
        sb.append(URL_PATH_WITH_INDEX);
    }

    @Override
    protected Table getTableWithHeader(RestRequest request) {
        Table table = new Table();
        table.startHeaders();
        table.addCell("index.name", "alias:n;desc:name of index");
        table.addCell("docs.counts", "alias:c;desc:count of documents");
        table.endHeaders();
        return table;
    }

    @Override
    public String getName() {
        return "cat_custom_indices";
    }

    private void getClusterHealth(String[] indices, NodeClient client, final ActionListener<ClusterHealthResponse> listener) {
        ClusterHealthRequest request = new ClusterHealthRequest();
        request.indices(indices);

        client.admin().cluster().health(request, listener);
    }

    private void getIndicesSettings(String[] indices, NodeClient client, final ActionListener<GetSettingsResponse> listener) {
        GetSettingsRequest request = new GetSettingsRequest();
        request.indices(indices);

        client.admin().indices().getSettings(request, listener);
    }

    private void getClusterState(String[] indices, NodeClient client, final ActionListener<ClusterStateResponse> listener) {
        ClusterStateRequest request = new ClusterStateRequest();
        request.indices(indices);

        client.admin().cluster().state(request, listener);
    }

    private void getIndicesStats(String[] indices, NodeClient client, final ActionListener<IndicesStatsResponse> listener) {
        IndicesStatsRequest request = new IndicesStatsRequest();
        request.indices(indices);

        client.admin().indices().stats(request, listener);

    }
}
