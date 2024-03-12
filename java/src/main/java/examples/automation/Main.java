// Copyright (c) 2023 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package examples.automation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;

import com.daml.ledger.api.v1.admin.UserManagementServiceGrpc;
import com.daml.ledger.api.v1.admin.UserManagementServiceGrpc.UserManagementServiceBlockingStub;
import com.daml.ledger.api.v1.admin.UserManagementServiceOuterClass.GetUserRequest;
import com.daml.ledger.api.v1.admin.UserManagementServiceOuterClass.GetUserResponse;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Processor.class.getName());

    public static void main(String[] args) {

        String appId = System.getenv("DAML_APPLICATION_ID");
        String ledgerId = System.getenv("DAML_LEDGER_ID");
        String userId = System.getenv("DAML_USER_ID");
        // Split the URL into host and port
        String[] url = System.getenv("DAML_LEDGER_URL").split(":");
        String host = url[0];
        int port = Integer.parseInt(url[1]);

        parseConfigFile();

        // Initialize a plaintext gRPC channel
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

        // Fetch the user's primary party
        UserManagementServiceBlockingStub userService = UserManagementServiceGrpc.newBlockingStub(channel);
        GetUserResponse user = userService.getUser(GetUserRequest.newBuilder().setUserId(userId).build());
        String primaryParty = user.getUser().getPrimaryParty();
        logger.info("Running as {} with primary party {}}", userId, primaryParty);

        // initialize the response processor
        Processor processor = new Processor(primaryParty, ledgerId, appId, channel);

        // start the processors asynchronously
        processor.runIndefinitely();

        try {
            // Run forever
            Thread.currentThread().join();
            System.exit(0);
        } catch (InterruptedException e) {
            logger.error(e.toString(), e );
        }
    }

    private static void parseConfigFile() {
        try {

            Optional<String> configFilePath = Optional.ofNullable(System.getenv("CONFIG_FILE"));

            if (configFilePath.isPresent()) {
                String configContent = Files.readString(Paths.get(configFilePath.get()));

                if (!configContent.isBlank()) {
                    logger.info(configContent);
                    JSONObject config = new JSONObject(configContent);
                    System.out.println("configFilePath: " + configFilePath);
                    System.out.println("configFileContents" + config.toString(4));
                } else {
                    throw new IOException("No config file content found");
                }
            }
            else {
                throw new IOException("No config file content found");
            }

        } catch (IOException | JSONException e) {
            // Catch any file read or JSON parsing errors in case the argument JSON file wasn't uploaded.
            // Since this is just an example we don't need to worry about that currently.
            logger.warn(e.toString(), e);
        }
    }
}
