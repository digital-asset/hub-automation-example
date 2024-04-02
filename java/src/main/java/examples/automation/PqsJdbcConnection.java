package examples.automation;

import org.postgresql.ds.PGSimpleDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PqsJdbcConnection {
    private final PGSimpleDataSource dataSource;

    public PqsJdbcConnection(String jdbcUrl) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        this.dataSource = new PGSimpleDataSource();
        dataSource.setUrl(jdbcUrl);
    }

    public void run() {

        try (var connection = dataSource.getConnection()) {
            Statement st = connection.createStatement();
            var selectActiveQuery = "select payload from active('User:User')";
            System.out.println("running query " + selectActiveQuery);
            // payload is returned as a JSON object
            // postgres sql provides the ‘->’ operator to retrieve values of the various keys in a JSON object.
            ResultSet rs = st.executeQuery(selectActiveQuery);
            while(rs.next()) {
                System.out.println("printing results: " + rs.getString(1)+ ',' + rs.getString(2));
            }
            rs.close();
            st.close();
        } catch (SQLException e) { //
            e.printStackTrace();
        }
        finally {
            System.out.println("connection is closed");
        }
    }
}

