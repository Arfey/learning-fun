package elasticsearchplugins.plugins.rest;

import elasticsearchplugins.plugins.rest.action.termlist.RestTermListAction;
import elasticsearchplugins.plugins.rest.action.termlist.RestTermListRequest;
import elasticsearchplugins.plugins.rest.action.termlist.RestTermListResponse;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.RestStatusToXContentListener;

import java.io.IOException;

import static org.elasticsearch.rest.RestRequest.Method.GET;

public class TermListRestHandler extends BaseRestHandler {
    static final String NAME = "term_list_plugin";

    public TermListRestHandler(RestController controller) {
        controller.registerHandler(GET, "/term_list", this);
        controller.registerHandler(GET, "{index}/term_list", this);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
        RestTermListRequest req = new RestTermListRequest();

        req.setField(request.param("field"));
        req.setIndex(request.param("index"));


        return channel -> {
            client.execute(
                    RestTermListAction.INSTANCE, req, new RestStatusToXContentListener<RestTermListResponse>(channel));
        };
    }
}
