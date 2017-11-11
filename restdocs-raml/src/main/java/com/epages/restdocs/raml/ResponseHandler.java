package com.epages.restdocs.raml;

import static java.util.Collections.emptyMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.util.StringUtils;

public class ResponseHandler extends AbstractFieldHandler {

    static final String RESPONSE_BODY_FILE_NAME_SUFFIX = "-response.json";

    @Override
    public Map<String, Object> generateModel(Operation operation, RamlResourceSnippetParameters parameters) {
        final OperationResponse response = operation.getResponse();
        if (!StringUtils.isEmpty(response.getContentAsString())) {
            Map<String, Object> model = new HashMap<>();
            model.put("responseBodyFileName", getResponseFileName(operation));
            model.put("responseBodyPresent", true);
            model.put("contentTypeResponse", response.getHeaders().getContentType().getType() + "/" + response.getHeaders().getContentType().getSubtype());
            if (!parameters.getResponseFieldDescriptors().isEmpty()) {
                validateResponseFieldsAndInferTypeInformation(operation, parameters);
                model.put("responseFields", transformDescriptorsToModel(parameters.getResponseFieldDescriptors()));
                model.put("responseFieldsPresent", true);
            }
            return model;
        }
        return emptyMap();
    }

    private void validateResponseFieldsAndInferTypeInformation(Operation operation, RamlResourceSnippetParameters parameters) {
        new ResponseFieldsSnippetWrapper(parameters.getResponseFieldDescriptors()).validateFieldsAndInferTypeInformation(operation);
    }

    private String getResponseFileName(Operation operation) {
        return operation.getName() + RESPONSE_BODY_FILE_NAME_SUFFIX;
    }

    /**
     * We need the wrapper to take advantage of the validation of fields and the inference of type information.
     *
     * This is baked into {@link org.springframework.restdocs.payload.AbstractFieldsSnippet#createModel(Operation)} and is not accessible separately.
     */
    static class ResponseFieldsSnippetWrapper extends ResponseFieldsSnippet {

        ResponseFieldsSnippetWrapper(List<FieldDescriptor> descriptors) {
            super(descriptors);
        }

        void validateFieldsAndInferTypeInformation(Operation operation) {
            super.createModel(operation);
        }
    }
}