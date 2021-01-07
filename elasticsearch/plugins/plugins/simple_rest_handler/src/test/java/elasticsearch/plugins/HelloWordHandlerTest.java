package elasticsearch.plugins;

import org.apache.lucene.util.SetOnce;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.concurrent.ThreadContext;
import org.elasticsearch.rest.AbstractRestChannel;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestResponse;
import org.elasticsearch.test.rest.FakeRestRequest;
import org.elasticsearch.test.rest.RestActionTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.elasticsearch.rest.RestRequest.Method.GET;

public class HelloWordHandlerTest extends RestActionTestCase {
    @Before
    public void setUpAction() {
        new HelloWordHandler(controller());
    }

    @Test
    public void testHelloWorldRequest() {
        RestRequest request = new FakeRestRequest.Builder(xContentRegistry())
                .withMethod(GET)
                .withPath("/hello")
                .build();

        RestResponse response = dispatchRequestTest(request);
        Assert.assertEquals("Hello, world!\n", response.content().utf8ToString());
    }

    @Test
    public void testHelloWithNamedRequest() {
        RestRequest request = new FakeRestRequest.Builder(xContentRegistry())
                .withMethod(GET)
                .withPath("/hello/mike")
                .build();

        RestResponse response = dispatchRequestTest(request);
        Assert.assertEquals("Hello, mike!\n", response.content().utf8ToString());
    }

    private RestResponse dispatchRequestTest(RestRequest req) {
        final SetOnce<RestResponse> responseSetOnce = new SetOnce<>();
        controller().dispatchRequest(req, new AbstractRestChannel(req, true) {
            @Override
            public void sendResponse(RestResponse response) {
                responseSetOnce.set(response);
            }
        }, new ThreadContext(Settings.EMPTY));

        return responseSetOnce.get();
    }
}