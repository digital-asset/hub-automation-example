Copyright (c) 2024 Digital Asset (Switzerland) GmbH and/or its affiliates. All Rights Reserved. SPDX-License-Identifier: 0BSD

# Sample Code

**This repo contains sample code to help you get started with Daml. Please bear
in mind that it is provided for illustrative purposes only, and as such may not
be production quality and/or may not fit your use-cases. You may use the
contents of this repo in parts or in whole according to the BSD0 license:**

> Copyright © 2024 Digital Asset (Switzerland) GmbH and/or its affiliates
>
> Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted.
>
> THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

> **This repository does not accept Pull Requests at the moment.**

## Example JVM Automation

This repository has a simple skeleton example of a JVM automation that can be run in Daml Hub. This can be copied and then adjusted to your application.

The Daml model in `example-model` has its templates generated into the source code via `daml codegen` - you can either build a model from this example or replace the
codegen and model with your own.

For more examples of how to use the Daml Java Bindings, please refer to https://github.com/digital-asset/ex-java-bindings.

### To build

```sh
  make all
```

Running this will use [maven](https://maven.apache.org/install.html) to build and package a 'fat' `.jar` file with the automation. It will then copy the file with the correct version and name (as set in the `Makefile`) into the root directory - the `.jar` file is what will be uploaded to Daml Hub to run the automation.

### To run locally

```sh
# start up a local sandbox
daml sandbox

# start up the jvm automation
./run_local.sh
```


## Structure


### pom.xml
https://github.com/digital-asset/hub-pythonbot-example/blob/51b05c41849dc98b821cc3a1244b0235f3bc0339/java/pom.xml#L1-L89

### Directory structure
```
├── Makefile
├── README.md
├── log
│   ├── canton.log
│   └── canton_errors.log
├── pom.xml
├── run_local.sh
├── src
│   └── main
│       ├── java
│       │   └── examples
│       │       └── automation
│       │           ├── Main.java
│       │           ├── Processor.java
│       │           └── codegen
│       │               ├── da
│       │               │   ├── internal
│       │               │   │   └── template
│       │               │   │       └── Archive.java
│       │               │   └── types
│       │               │       └── Tuple2.java
│       │               ├── main
│       │               │   ├── JavaAutomationRequest.java
│       │               │   ├── JavaAutomationRequest_Accept.java
│       │               │   ├── JavaAutomationResponse.java
│       │               └── user
│       │                   ├── Acknowledge.java
│       │                   ├── Alias.java
│       │                   ├── Change.java
│       │                   ├── Follow.java
│       │                   ├── Notification.java
│       │                   └── User.java
│       └── resources
│           └── logback.xml
```

### `main`

```java
public class Main {

    public static void main(String[] args) {

        String appId = System.getenv("DAML_APPLICATION_ID");
        String ledgerId = System.getenv("DAML_LEDGER_ID");
        String userId = System.getenv("DAML_USER_ID");
        String url[] = System.getenv("DAML_LEDGER_URL").split(":");
        String host = url[0];
        int port = Integer.parseInt(url[1]);
    }
}
```

## Configuration

You can configure an automation that may differ from deployment to deployment by uploading a configuration file when the automation is deployed.

This configuration can be of any type, for example `.json`, `.toml`, `.yaml`.

To access this configuration file from the automation, set the environment variable `CONFIG_FILE` to point to a location on the volume where the configuration file is stored. This can then be read by the automation and parsed based on what type of file was uploaded.

For example if a JSON file was uploaded:
```java
        try {
            String configFilePath = System.getenv("CONFIG_FILE");
            String configContent = Files.readString(Paths.get(configFilePath));
            JSONObject config = new JSONObject(configContent);

            System.out.println("configFilePath: " + configFilePath);
            System.out.println("configFileContents" + config.toString());

        } catch (IOException | JSONException e) {
            // Catch any file read or JSON parsing errors in case the argument JSON file wasn't uploaded.
            e.printStackTrace();
        }
```

## External Connectivity
JVM automations running on ledgers owned by Enterprise users can connect to services running on the internet outside of Daml Hub. The outgoing IP address is dynamically set.

For incoming connections, Daml Hub provides a webhook URL of`http://{ledgerId}.daml.app/automation/{instanceId}/`. This link can be copied from the Status Page for the running instance.

If you would like to accept traffic to that endpoint, you can run a webserver running on the default `0.0.0.0:8080`. A request pointed directly to the webhook URL will be routed to the root `/` of your server.


## Automation Code
JVM automations running on Daml Hub generally use the [Daml Java Bindings](https://docs.daml.com/app-dev/bindings-java/index.html#java-bindings) to react to incoming Daml contracts.


## Participant Query Store (PQS) Usage

A JVM automation can also query the PQS Postgres database if you want to take an action based on a particular state of the ledger.

PQS queries are also the recommended way of reading data if you need to query for archived historical information, rather than relying on the stream of gRPC transactions.


### Connecting to Participant Query Store (PQS)

The full JDBC URL connection to the Postgres database is made available to your running JVM automation as the environment variable `PQS_JDBC_URL`

You can read from this environment variable and then create a Postgres database connection using your JDBC driver of choice.


Simple example of connecting to a PQS database using JDBC:

```java
    import org.postgresql.ds.PGSimpleDataSource;
    import java.sql.*;

    String jdbcUrl = System.getenv("PQS_JDBC_URL");
    PqsJdbcConnection pqsJdbcConnection = new PqsJdbcConnection(jdbcUrl);

    public class PqsJdbcConnection {
        private final PGSimpleDataSource dataSource;

        public PqsJdbcConnection(String jdbcUrl) throws ClassNotFoundException {
            Class.forName("org.postgresql.Driver");
            this.dataSource = new PGSimpleDataSource();
            dataSource.setUrl(jdbcUrl);
        }

        public ResultSet queryUserContracts(String query) {
            try {
                Connection connection = dataSource.getConnection();
                Statement st = connection.createStatement();
                // The template below must be prefixed with the Package ID if there is more than
                // one package with the template on the participant.
                return st.executeQuery("select payload from active('User:User')");
            } catch (SQLException e) {
                System.out.println("hit an exception: " + e.toString());
                throw new RuntimeException(e);
            }
        }
    }
```

A more concrete example of how PQS can be used with reusable code to query aactive and archived templates can be found in [PqsJdbcConnect.java](src/main/java/examples/automation/PqsJdbcConnection.java).

More guidance on how to query the PQS is detailed in the [Participant Query Store (PQS) documentation](https://hub.daml.com/docs/quickstart#participant-query-store-pqs).
