package examples.automation;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.net.HttpURLConnection;

public class PqsHttpServer {
    HttpServer server = null;

    PqsHttpServer(PqsJdbcConnection pqsConnection) {

        try {
            this.server = HttpServer.create(new java.net.InetSocketAddress("0.0.0.0", 8080), 0);
            server.setExecutor(null);
            System.out.printf("Server binding on port %s and %s", 8080, server.getAddress());

            server.createContext("/active/users", exchange ->
                this.handlePqsQuery(exchange, pqsConnection.getActiveUsers())
            );

            server.createContext("/archived/notifications", exchange ->
                this.handlePqsQuery(exchange, pqsConnection.getArchivedNotifications())
            );
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
