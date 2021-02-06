package elasticsearchplugins.plugins.rest.action.termlist;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.StatusToXContentObject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;

public class RestTermListResponse extends ActionResponse implements StatusToXContentObject {
    public RestTermListResponse(StreamInput in) throws IOException {
        super(in);
    }

    public RestTermListResponse() {}

    @Override
    public void writeTo(StreamOutput out) throws IOException {

    }

    @Override
    public RestStatus status() {
        return null;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        return null;
    }
}
