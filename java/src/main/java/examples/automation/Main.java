// Copyright (c) 2023 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package examples.automation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.daml.ledger.api.v1.admin.UserManagementServiceGrpc;
import com.daml.ledger.api.v1.admin.UserManagementServiceGrpc.UserManagementServiceBlockingStub;
import com.daml.ledger.api.v1.admin.UserManagementServiceOuterClass.GetUserRequest;
import com.daml.ledger.api.v1.admin.UserManagementServiceOuterClass.GetUserResponse;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class Main {
    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        String appId = System.getenv("DAML_APPLICATION_ID");
        String ledgerId = System.getenv("DAML_LEDGER_ID");
        String userId = System.getenv("DAML_USER_ID");
        String[] url = System.getenv("DAML_LEDGER_URL").split(":");
        String host = url[0];
        int port = Integer.parseInt(url[1]);

        try {
            String configFilePath = System.getenv("CONFIG_FILE");
            String configContent = Files.readString(Paths.get(configFilePath));
            JSONObject config = new JSONObject(configContent);

            System.out.println("configFilePath: " + configFilePath);
            System.out.println("configFileContents" + config);

        } catch (IOException | JSONException e) {
            // Catch any file read or JSON parsing errors in case the argument JSON file wasn't uploaded.
            // Since this is just an example we don't need to worry about that currently.
            LOGGER.log( Level.SEVERE, e.toString(), e );
        }

        // Initialize a plaintext gRPC channel
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

        // Fetch the user's primary party
        UserManagementServiceBlockingStub userService = UserManagementServiceGrpc.newBlockingStub(channel);
        GetUserResponse user = userService.getUser(GetUserRequest.newBuilder().setUserId(userId).build());
        String primaryParty = user.getUser().getPrimaryParty();
        System.out.printf("Running as %s with primary party %s", userId, primaryParty);

        // initialize the response processor
        Processor processor = new Processor(primaryParty, ledgerId, appId, channel);

        // start the processors asynchronously
        processor.runIndefinitely();

        try {
            // Run forever
            Thread.currentThread().join();
            System.exit(0);
        } catch (InterruptedException e) {
            LOGGER.log( Level.SEVERE, e.toString(), e );
        }
    }
}
