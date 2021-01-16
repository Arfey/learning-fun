package elasticsearchplugins;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.Table;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.rest.action.cat.AbstractCatAction;

import static org.elasticsearch.rest.RestRequest.Method.GET;

public class RestIndicesActionHandler extends AbstractCatAction {
    private static String URL_PATH = "/_cat/custom_indices";

    public RestIndicesActionHandler(RestController controller) {
        controller.registerHandler(GET, URL_PATH, this);
    }

    @Override
    protected RestChannelConsumer doCatRequest(RestRequest request, NodeClient client) {
        return channel -> {
            channel.sendResponse(new BytesRestResponse(RestStatus.OK, "text"));
        };
    }

    @Override
    protected void documentation(StringBuilder sb) {
        sb.append(URL_PATH);
    }

    @Override
    protected Table getTableWithHeader(RestRequest request) {
        Table table = new Table();
        table.startHeaders();
        table.addCell("cell name");
        table.endHeaders();
        return table;
    }

    @Override
    public String getName() {
        return "cat_custom_indices";
    }
}
