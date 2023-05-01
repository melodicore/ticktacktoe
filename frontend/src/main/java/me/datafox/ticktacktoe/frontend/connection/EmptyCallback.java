package me.datafox.ticktacktoe.frontend.connection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author datafox
 */
public interface EmptyCallback extends ConnectionCallback<String> {
    static Builder builder() {
        return new Builder();
    }

    class Builder {
        Runnable completed;
        Consumer<String> failed;
        Map<Integer,String> httpResponses;

        private Builder() {
        }


        public EmptyCallback.Builder completed(Runnable completed) {
            this.completed = completed;
            return this;
        }

        public EmptyCallback.Builder failed(Consumer<String> failed) {
            this.failed = failed;
            return this;
        }

        public EmptyCallback.Builder response(int code, String status) {
            if(httpResponses == null) httpResponses = new HashMap<>();
            httpResponses.put(code, status);
            return this;
        }

        public EmptyCallback.Builder responses(Map<Integer,String> responses) {
            if(httpResponses == null) httpResponses = new HashMap<>();
            httpResponses.putAll(responses);
            return this;
        }

        public EmptyCallback build() {
            return new EmptyCallback() {
                @Override
                public Class<String> getType() {
                    return String.class;
                }

                @Override
                public void completed(String result) {
                    if(completed != null) completed.run();
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
