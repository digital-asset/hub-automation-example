package examples.automation;

import com.daml.ledger.javaapi.data.Identifier;
import com.sun.net.httpserver.Headers;
import examples.automation.codegen.user.User;
import examples.automation.codegen.user.Notification;
import examples.automation.codegen.user.Alias;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import org.json.JSONObject;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.net.HttpURLConnection;

public class PqsHttpServer {
    private static final Logger logger = LoggerFactory.getLogger(PqsHttpServer.class.getName());
    HttpServer server = null;

    PqsHttpServer(PqsJdbcConnection pqsConnection) {

        try {
            this.server = HttpServer.create(new java.net.InetSocketAddress("0.0.0.0", 8080), 0);
            server.setExecutor(null);
            System.out.printf("Server binding on port %s and %s", 8080, server.getAddress());

            // endpoints for active contracts
            server.createContext("/active/users", exchange ->
                    this.handlePqsQuery(exchange, pqsConnection.getActiveContracts(User.TEMPLATE_ID))
            );

            server.createContext("/active/notifications", exchange ->
                    this.handlePqsQuery(exchange, pqsConnection.getActiveContracts(Notification.TEMPLATE_ID))
            );

            server.createContext("/active/aliases", exchange ->
                    this.handlePqsQuery(exchange, pqsConnection.getActiveContracts(Alias.TEMPLATE_ID))
            );

            // endpoints for archived contracts
            server.createContext("/archives/users", exchange ->
                    this.handlePqsQuery(exchange, pqsConnection.getArchivedContracts(User.TEMPLATE_ID))
            );

            server.createContext("/archives/notifications", exchange ->
                    this.handlePqsQuery(exchange, pqsConnection.getArchivedContracts(Notification.TEMPLATE_ID))
            );

            server.createContext("/archives/aliases", exchange ->
                    this.handlePqsQuery(exchange, pqsConnection.getArchivedContracts(Alias.TEMPLATE_ID))
            );

            server.createContext("/redact", exchange -> {
                try {
                    handleRedaction(exchange, pqsConnection, Notification.TEMPLATE_ID);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            server.createContext("/dropIndex", exchange -> {
                try {
                    handleDropIndex(exchange, pqsConnection);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePqsQuery(HttpExchange exchange, List<JSONObject> contracts) {
        try {
            OutputStream os = exchange.getResponseBody();
            String archivedNotifications = new JSONArray(contracts).toString();
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, archivedNotifications.getBytes().length);
            os.write(archivedNotifications.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //example for a post request
    private void handleRedaction(HttpExchange exchange, PqsJdbcConnection connect, Identifier template) throws IOException {
        String method = exchange.getRequestMethod();
        String response = "Request Received";
        try {
            if (method.equals("POST")) {
                InputStreamReader inStream = new InputStreamReader(exchange.getRequestBody());
                BufferedReader br = new BufferedReader(inStream);

                int b;
                StringBuilder buf = new StringBuilder(512);
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }

                br.close();
                inStream.close();
                JSONObject jsonBody = new JSONObject(buf.toString());
                String contractId = jsonBody.get("contractId").toString();

                response = connect.redactByContractId(contractId, template).toString();
            } else {
                throw new Exception("Not valid request method");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            response = e.toString();
        }

        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Access-Control-Allow-Origin", "*");
        responseHeaders.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        responseHeaders.add("Access-Control-Allow-Credentials", "true");
        responseHeaders.add("Access-Control-Allow-Methods", "GET, POST");

        //Sending back response to the client
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream outStream = exchange.getResponseBody();
        outStream.write(response.getBytes());
        outStream.close();
    }

    //drop index
    private void handleDropIndex(HttpExchange exchange, PqsJdbcConnection connect) throws IOException {
        String method = exchange.getRequestMethod();
        String response = "Request Received";
        try {
            if (method.equals("POST")) {
                InputStreamReader inStream = new InputStreamReader(exchange.getRequestBody());
                BufferedReader br = new BufferedReader(inStream);

                int b;
                StringBuilder buf = new StringBuilder(512);
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }

                br.close();
                inStream.close();
                JSONObject jsonBody = new JSONObject(buf.toString());
                String contractId = jsonBody.get("indexName").toString();

                response = connect.dropIndex(contractId).toString();
            } else {
                throw new Exception("Not valid request method");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            response = e.toString();
        }

        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Access-Control-Allow-Origin", "*");
        responseHeaders.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        responseHeaders.add("Access-Control-Allow-Credentials", "true");
        responseHeaders.add("Access-Control-Allow-Methods", "GET, POST");

        //Sending back response to the client
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream outStream = exchange.getResponseBody();
        outStream.write(response.getBytes());
        outStream.close();
    }

    public void start() {
        try {
            System.out.println("Server started");
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            server.stop(15);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
