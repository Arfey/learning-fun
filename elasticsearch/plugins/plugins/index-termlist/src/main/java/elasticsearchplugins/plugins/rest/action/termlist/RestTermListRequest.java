package elasticsearchplugins.plugins.rest.action.termlist;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;

public class RestTermListRequest extends ActionRequest {
    private String index;
    private String field;

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndex() {
        return this.index;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getField() {
        return this.field;
    }

}
