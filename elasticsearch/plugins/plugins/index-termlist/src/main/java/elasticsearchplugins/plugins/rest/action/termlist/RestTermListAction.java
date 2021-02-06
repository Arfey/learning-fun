package elasticsearchplugins.plugins.rest.action.termlist;

import org.elasticsearch.action.ActionType;
import org.elasticsearch.common.io.stream.Writeable;

public class RestTermListAction extends ActionType<RestTermListResponse> {

    public static final String NAME = "term_list";

    public static final RestTermListAction INSTANCE = new RestTermListAction();

    /**
     * @param name                          The name of the action, must be unique across actions.
     * @param restoreSnapshotResponseReader A reader for the response type
     */
    public RestTermListAction(String name, Writeable.Reader<RestTermListResponse> restoreSnapshotResponseReader) {
        super(name, restoreSnapshotResponseReader);
    }

    public RestTermListAction() {
        super(NAME, RestTermListResponse::new);
    }

}
