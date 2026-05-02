package com.promo.otp.server;

import com.promo.otp.controller.AdminController;
import com.promo.otp.controller.AuthController;
import com.promo.otp.controller.OtpController;
import com.promo.otp.model.ApiResponse;
import com.promo.otp.security.AuthFilter;
import com.promo.otp.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpServerManager {
    private static final Logger logger = LoggerFactory.getLogger(HttpServerManager.class);
    private final int port;
    private HttpServer server;

    private final AuthController authController;
    private final OtpController otpController;
    private final AdminController adminController;

    public HttpServerManager(int port) {
        this.port = port;
        this.authController = new AuthController();
        this.otpController = new OtpController();
        this.adminController = new AdminController();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newCachedThreadPool());

        server.createContext("/api/health", new HealthHandler());
        server.createContext("/api/auth/register", wrap(authController::register));
        server.createContext("/api/auth/login", wrap(authController::login));
        server.createContext("/api/otp/generate", wrap(otpController::generate));
        server.createContext("/api/otp/validate", wrap(otpController::validate));
        server.createContext("/api/admin/config", new AdminConfigHandler());
        server.createContext("/api/admin/users", new AdminUsersHandler());

        server.start();
        logger.info("HTTP Server started on port {}", port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            logger.info("HTTP Server stopped");
        }
    }

    private class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = JsonUtil.toJson(ApiResponse.success("Service is running"));
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    private class AdminConfigHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            AuthFilter.authenticate(exchange, new HttpHandler() {
                @Override
                public void handle(HttpExchange e) throws IOException {
                    try {
                        if ("GET".equals(e.getRequestMethod())) {
                            adminController.getConfig(e);
                        } else if ("PUT".equals(e.getRequestMethod())) {
                            adminController.updateConfig(e);
                        } else {
                            sendMethodNotAllowed(e);
                        }
                    } catch (Exception ex) {
                        sendError(e, 500, "Internal server error");
                    }
                }
            });
        }
    }

    private class AdminUsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            AuthFilter.authenticate(exchange, new HttpHandler() {
                @Override
                public void handle(HttpExchange e) throws IOException {
                    try {
                        if ("GET".equals(e.getRequestMethod())) {
                            adminController.getAllUsers(e);
                        } else if ("DELETE".equals(e.getRequestMethod())) {
                            adminController.deleteUser(e);
                        } else {
                            sendMethodNotAllowed(e);
                        }
                    } catch (Exception ex) {
                        sendError(e, 500, "Internal server error");
                    }
                }
            });
        }
    }

    private HttpHandler wrap(ApiHandler handler) {
        return exchange -> AuthFilter.authenticate(exchange, new HttpHandler() {
            @Override
            public void handle(HttpExchange e) throws IOException {
                try {
                    handler.handle(e);
                } catch (Exception ex) {
                    logger.error("Handler error", ex);
                    sendError(e, 500, "Internal server error");
                }
            }
        });
    }

    private void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        String response = JsonUtil.toJson(ApiResponse.error("Method not allowed"));
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(405, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        String response = JsonUtil.toJson(ApiResponse.error(message));
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    @FunctionalInterface
    interface ApiHandler {
        void handle(HttpExchange exchange) throws Exception;
    }
}
