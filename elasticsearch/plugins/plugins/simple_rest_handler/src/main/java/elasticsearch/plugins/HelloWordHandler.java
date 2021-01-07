package elasticsearch.plugins;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;

import static org.elasticsearch.rest.RestRequest.Method.GET;

public class HelloWordHandler extends BaseRestHandler {
    private static final String USER_NAME_PARAM = "name";

    public HelloWordHandler(RestController controller) {
        controller.registerHandler(GET, String.format("/hello/{%s}", USER_NAME_PARAM), this);
        controller.registerHandler(GET, "/hello", this);
    }

    @Override
    public String getName() {
        return "hello_world_handler";
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
        String userName = request.param(USER_NAME_PARAM, "world");
        return channel -> {
            String message = String.format("Hello, %s!\n", userName);
            channel.sendResponse(new BytesRestResponse(RestStatus.OK, message));
        };
    }
}
