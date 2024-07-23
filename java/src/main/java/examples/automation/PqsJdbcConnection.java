package examples.automation;

import org.json.JSONObject;
import org.json.XML;
import org.postgresql.ds.PGSimpleDataSource;

import com.daml.ledger.javaapi.data.Identifier;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class PqsJdbcConnection {
    private final PGSimpleDataSource dataSource;

    public PqsJdbcConnection(String jdbcUrl) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        this.dataSource = new PGSimpleDataSource();
        System.out.println("jdbcUrl: " + jdbcUrl);
        dataSource.setUrl(jdbcUrl);
    }

    public List<Map<String, Object>> runQuery(String query) {
        try (
                Connection connection = dataSource.getConnection();
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(query);
        ) {
            System.out.println("running query " + query);

            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
            Map<String, Object> row = null;

            ResultSetMetaData metaData = rs.getMetaData();
            Integer columnCount = metaData.getColumnCount();

            while (rs.next()) {
                row = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    System.out.println("column name: " + metaData.getColumnName(i) + ", value: " + rs.getObject(i));
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                resultList.add(row);
            }
            System.out.println("result set size is " + resultList.size());

            return resultList;

        } catch (SQLException e) {
            System.out.println("hit an exception: " + e.toString());
            throw new RuntimeException(e);
        }
    }

    public int callableStatement(String query) throws SQLException {
        try {
            Connection connection = dataSource.getConnection();
            CallableStatement st = connection.prepareCall(query);
            System.out.println("executing callable statement: " +  query);
            return st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
            throw new SQLException(e);
        }
    }

    private String makeTemplateName(Identifier template) {
        return String.format("%s:%s:%s", template.getPackageId(), template.getModuleName(), template.getEntityName());
    }

    //pqsreader queries

    private List<JSONObject> getContracts(String query) {
        // payload is returned as a JSON object
        // postgres sql provides the ‘->’ operator to retrieve values of the various keys in a JSON object.
        List<Map<String, Object>> list = runQuery(query);
        List<JSONObject> jsonList = new ArrayList<>();
        list.forEach(obj -> jsonList.add(new JSONObject(obj.get("payload").toString())));
        return jsonList;
    }

    public List<JSONObject> getActiveContracts(Identifier template) {
        return this.getContracts(String.format("select payload from active('%s')", makeTemplateName(template)));
    }

    public List<JSONObject> getArchivedContracts(Identifier template) {
        return this.getContracts(String.format("select payload from archives('%s')", makeTemplateName(template)));
    }

    //redaction example
    public JSONArray redactByContractId(String contractId, Identifier template) throws SQLException {

        String redactionId =  UUID.randomUUID().toString();
        List<String> contractsList = new ArrayList<String>();
        String redactionStoredProc = String.format("select redact_contract('%s', '%s') from archives('%s')", contractId, redactionId, makeTemplateName(template));
        List<Map<String, Object>> list = runQuery(redactionStoredProc);
        list.forEach(contracts -> contracts.forEach((y, payload) -> contractsList.add(payload.toString())));
        JSONArray redactedValue = new JSONArray(contractsList);
        logger.info("Redacted value: " + redactedValue);
        return redactedValue;
    }


    private String createIndexQuery(String indexName, String qualifiedName, String expression, String indexType) {
        return String.format("call create_index_for_contract('%s', '%s', '%s', '%s')", indexName, qualifiedName, expression, indexType);
    }

    //create index over payload on table partition for corresponding qualified Daml entity

    public JSONArray createIndex(Identifier template) throws SQLException {
        List<String> contractsList = new ArrayList<String>();

        String createIndexStatement = createIndexQuery("example_model_request_id",
                "example-model:" + makeTemplateName(template),
                   "((payload->>''party'')::text)",
                   "btree");
        int s = callableStatement(createIndexStatement);
        contractsList.add(s,toString());
        return new JSONArray(contractsList);
    }

}

