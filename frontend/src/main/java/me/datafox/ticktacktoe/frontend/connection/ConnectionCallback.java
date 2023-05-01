package me.datafox.ticktacktoe.frontend.connection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author datafox
 */
public interface ConnectionCallback<T> {
    static <T> Builder<T> builder(Class<T> type) {
        return new Builder<>(type);
    }

    Class<T> getType();

    void completed(T result);

    void failed(String reason);

    class Builder<T> {
        final Class<T> type;
        Consumer<T> completed;
        Consumer<String> failed;
        Map<Integer,String> httpResponses;

        protected Builder(Class<T> type) {
            this.type = type;
        }

        public Builder<T> completed(Consumer<T> completed) {
            this.completed = completed;
            return this;
        }

        public Builder<T> failed(Consumer<String> failed) {
            this.failed = failed;
            return this;
        }

        public Builder<T> response(int code, String status) {
            if(httpResponses == null) httpResponses = new HashMap<>();
            httpResponses.put(code, status);
            return this;
        }

        public Builder<T> responses(Map<Integer,String> responses) {
            if(httpResponses == null) httpResponses = new HashMap<>();
            httpResponses.putAll(responses);
            return this;
        }

        public ConnectionCallback<T> build() {
            return new ConnectionCallback<>() {
                @Override
                public Class<T> getType() {
                    return type;
                }

                @Override
                public void completed(T result) {
                    if(completed != null) completed.accept(result);
                }

                @Override
                public void failed(String reason) {
                    if(failed != null) {
                        if(reason.length() == 3 && httpResponses != null) try {
                            int code = Integer.parseInt(reason);
                            String status = httpResponses.get(code);
                            if(status != null) {
                                failed.accept(status);
                                return;
                            }
                        } catch(NumberFormatException ignored) {}
                        failed.accept(reason);
                    }
                }
            };
        }
    }
}
