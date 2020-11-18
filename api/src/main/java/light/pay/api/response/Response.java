package light.pay.api.response;

import light.pay.api.errors.Error;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Value
public class Response<T> {
    private boolean success;
    private T data;
    private List<Error> errors;

    public static <U> Response<U> createSuccessResponse(U data) {
        return new Response(true, data, Collections.emptyList());
    }

    public static Response createErrorResponse(List<Error> errors) {
        return new Response(false, null, errors);
    }

    public static Response createErrorResponse(String code, String entity, String cause) {
        return new Response(false, null, Collections.singletonList(Error.builder()
                .code(code)
                .entity(entity)
                .cause(cause)
                .build()));
    }

    public <U> Response<U> map(Function<? super T, ? extends U> mapper) {
        if (isSuccess()) {
            return createSuccessResponse(mapper.apply(this.getData()));
        }
        return (Response<U>) this;
    }

    public Response<T> validate(Predicate<? super T> validator, List<Error> errors) {
        if (isSuccess()) {
            return validator.test(this.getData()) ? this : createErrorResponse(errors);
        }
        return this;
    }

    public <U> Response<U> flatMap(Function<? super T, Response<U>> mapper) {
        if (isSuccess()) {
            return mapper.apply(this.getData());
        }
        return (Response<U>) this;
    }

    public T orElse(T other) {
        return isSuccess() ? data : other;
    }

    public Response<T> recoverWith(Function<List<Error>, Response<T>> mapper) {
        if (isSuccess()) {
            return this;
        }

        return mapper.apply(this.errors);
    }

    public Response<T> peek(Consumer<T> onSuccess, Consumer<List<Error>> onError) {
        if (isSuccess()) {
            onSuccess.accept(this.data);
        } else {
            onError.accept(this.errors);
        }

        return this;
    }
}
