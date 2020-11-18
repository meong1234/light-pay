package light.pay.server.transformer;

import light.pay.commons.marshalling.JsonUtils;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {

    @Override
    public String render(Object model) throws Exception {

        if (model != null && model.getClass() == String.class) {
            return (String) model;
        }

        return JsonUtils.toJson(model);
    }
}
