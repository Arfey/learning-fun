package com.luceneSearch.TermVectorSimilarity;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;

public class TextFieldWithTermVector extends Field {

    static final FieldType TEXT_TERM_VECTOR_TYPE = new FieldType(TextField.TYPE_STORED);

    static {
        TEXT_TERM_VECTOR_TYPE.setStoreTermVectorOffsets(true);
        TEXT_TERM_VECTOR_TYPE.setStoreTermVectors(true);
        TEXT_TERM_VECTOR_TYPE.freeze();
    }

    protected TextFieldWithTermVector(String name, String value) {
        super(name, value, TEXT_TERM_VECTOR_TYPE);
    }
}
