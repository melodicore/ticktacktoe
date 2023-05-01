package me.datafox.ticktacktoe.frontend.connection;

import lombok.Getter;
import me.datafox.ticktacktoe.frontend.Game;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author datafox
 */
public class RestHandler {
    public static LoginCallbackBuilder loginCallback(Consumer<String> token) {
        return new LoginCallbackBuilder(token);
    }

    private String address;
    @Getter
    private final BasicCookieStore cookies;

    public RestHandler() {
        cookies = new BasicCookieStore();
    }

    public void connect(String address, ConnectionCallback<String> callback) {
        this.address = address;
        get("/version", callback);
    }

    public void login(String username, String password, LoginCallback callback) {
        request(ClassicRequestBuilder.post("http://" + address + "/login")
                .setEntity(new UrlEncodedFormEntity(List.of(
                        new BasicNameValuePair("username", username),
                        new BasicNameValuePair("password", password)))), callback);
    }

    public <T> void get(String endpoint, ConnectionCallback<T> callback) {
        request(ClassicRequestBuilder.get("http://" + address + endpoint), callback);
    }

    public <T1, T2> void post(String endpoint, T1 requestEntity, ConnectionCallback<T2> callback) {
        request(ClassicRequestBuilder.post("http://" + address + endpoint)
                .setEntity(Game.json().toJson(requestEntity), ContentType.APPLICATION_JSON), callback);
    }

    private <T> void request(ClassicRequestBuilder builder, ConnectionCallback<T> callback) {
        Game.scheduler().execute(() -> {
            try(CloseableHttpClient client = HttpClientBuilder.create().setDefaultCookieStore(cookies).build()) {
                String result = client.execute(builder.build(), new BasicHttpClientResponseHandler());
                System.out.println(result);
                if(callback instanceof EmptyCallback empty) empty.completed("");
                else if(callback instanceof LoginCallback login) {
                    String token = cookies.getCookies().stream()
                            .filter(cookie -> cookie.getName().equals("JSESSIONID"))
                            .map(Cookie::getValue)
                            .findAny()
                            .orElseThrow();
                    login.token(token);
                    login.completed("");
                } else callback.completed(Game.json().fromJson(callback.getType(), result));
            } catch(HttpHostConnectException e) {
                callback.failed("Could not connect to host (connection refused)");
            } catch(UnknownHostException e) {
                callback.failed("Could not connect to host (not found)");
            } catch(HttpResponseException e) {
                callback.failed(e.getMessage().substring(13));
            } catch(Exception e) {
                callback.failed(e.getClass().getName() + ": " + e.getMessage());
            }
        });
    }

    public interface LoginCallback extends ConnectionCallback<String> {
        void token(String token);
    }


    public static class LoginCallbackBuilder extends ConnectionCallback.Builder<String> {
        private final Consumer<String> token;

        private LoginCallbackBuilder(Consumer<String> token) {
            super(String.class);
            this.token = token;
        }

        @Override
        public LoginCallbackBuilder completed(Consumer<String> completed) {
            return (LoginCallbackBuilder) super.completed(completed);
        }

        @Override
        public LoginCallbackBuilder failed(Consumer<String> failed) {
            return (LoginCallbackBuilder) super.failed(failed);
        }

        @Override
        public LoginCallbackBuilder response(int code, String status) {
            if(httpResponses == null) httpResponses = new HashMap<>();
            httpResponses.put(code, status);
            return this;
        }

        @Override
        public LoginCallbackBuilder responses(Map<Integer,String> responses) {
            if(httpResponses == null) httpResponses = new HashMap<>();
            httpResponses.putAll(responses);
            return this;
        }

        @Override
        public LoginCallback build() {
            return new LoginCallback() {
                @Override
                public void token(String token) {
                    LoginCallbackBuilder.this.token.accept(token);
                }

                @Override
                public Class<String> getType() {
                    return String.class;
                }

                @Override
                public void completed(String result) {
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