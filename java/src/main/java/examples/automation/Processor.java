// Copyright (c) 2023 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package examples.automation;

import com.daml.ledger.api.v1.*;
import com.daml.ledger.api.v1.CommandSubmissionServiceGrpc.CommandSubmissionServiceBlockingStub;
import com.daml.ledger.api.v1.EventOuterClass.Event;
import com.daml.ledger.api.v1.TransactionOuterClass.Transaction;
import com.daml.ledger.api.v1.TransactionServiceGrpc.TransactionServiceStub;
import com.daml.ledger.api.v1.TransactionServiceOuterClass.GetTransactionsResponse;
import com.daml.ledger.javaapi.data.*;
import com.daml.ledger.javaapi.data.codegen.Contract;
import com.daml.ledger.javaapi.data.codegen.ContractCompanion;
import com.daml.ledger.javaapi.data.codegen.Exercised;
import com.daml.ledger.javaapi.data.codegen.Update;

import com.google.protobuf.Empty;
import examples.automation.codegen.user.Notification;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class subscribes to the stream of transactions for a given party and reacts to Ping or Pong contracts.
 */
public class Processor {
    private final static Logger LOGGER = Logger.getLogger(Processor.class.getName());

    private final String party;
    private final String ledgerId;

    private final TransactionServiceStub transactionService;
    private final CommandSubmissionServiceBlockingStub submissionService;

    private final Identifier requestIdentifier;

    private final String appId;

    public Processor(String party, String ledgerId, String appId, ManagedChannel channel) {
        this.party = party;
        this.ledgerId = ledgerId;
        this.transactionService = TransactionServiceGrpc.newStub(channel);
        this.submissionService = CommandSubmissionServiceGrpc.newBlockingStub(channel);
        this.requestIdentifier = Notification.TEMPLATE_ID;
        this.appId = appId;
    }

    public void runIndefinitely() {
        // restrict the subscription to ping and pong template types through an inclusive filter
        final var inclusiveFilter = InclusiveFilter
                .ofTemplateIds(Set.of(requestIdentifier));
        // specify inclusive filter for the party attached to this processor
        final var getTransactionsRequest = getGetTransactionsRequest(inclusiveFilter);

        // this StreamObserver reacts to transactions and prints a message if an error occurs or the stream gets closed
        StreamObserver<GetTransactionsResponse> transactionObserver = new StreamObserver<>() {
            @Override
            public void onNext(GetTransactionsResponse value) {
                value.getTransactionsList().forEach(Processor.this::processTransaction);
            }

            @Override
            public void onError(Throwable t) {
                System.err.printf("%s encountered an error while processing transactions!\n", party);
                LOGGER.log( Level.SEVERE, t.toString(), t );
                System.exit(0);
            }

            @Override
            public void onCompleted() {
                System.out.printf("%s's transactions stream completed.\n", party);
            }
        };
        System.out.printf("%s starts reading transactions.\n", party);
        transactionService.getTransactions(getTransactionsRequest.toProto(), transactionObserver);
    }

    private GetTransactionsRequest getGetTransactionsRequest(InclusiveFilter inclusiveFilter) {
        final var filtersByParty = new FiltersByParty(Map.of(party, inclusiveFilter));

        // assemble the request for the transaction stream
        // NOTE: This will only respond to contracts created _after_ the bot was started
        return new GetTransactionsRequest(
                ledgerId,
                LedgerOffset.LedgerEnd.getInstance(),
                filtersByParty,
                true
        );
    }

    /**
     * Processes a transaction and sends the resulting commands to the Command Submission Service
     *
     * @param tx the Transaction to process
     */
    private void processTransaction(Transaction tx) {
        List<Command> commands = tx.getEventsList().stream()
                .filter(Event::hasCreated).map(Event::getCreated)
                .flatMap(e -> processEvent(tx.getWorkflowId(), e))
                .collect(Collectors.toList());

        if (!commands.isEmpty()) {
            CommandsSubmission commandsSubmission = CommandsSubmission.create(
                            this.appId,
                            UUID.randomUUID().toString(),
                            commands)
                    .withActAs(List.of(party))
                    .withReadAs(List.of(party))
                    .withWorkflowId(tx.getWorkflowId());
            Empty result = submissionService.submit(SubmitRequest.toProto(ledgerId, commandsSubmission));
            LOGGER.info(result.toString());
        }
    }

    private Stream<Command> processEvent(String workflowId, EventOuterClass.CreatedEvent protoEvent) {
        String contractId = protoEvent.getContractId();
        String choice = "Acknowledge";

        System.out.printf("%s is exercising %s on %s in workflow %s at count\n", party, choice, contractId, workflowId);

        final var event = CreatedEvent.fromProto(protoEvent);

        return  processRequest(
                        Notification.COMPANION,
                        Notification.Exercises::exerciseAcknowledge,
                        event);

    }

    private <Ct extends Contract<Id, Data>, Id, Data>
        Stream<Command> processRequest(
                ContractCompanion<Ct, Id, Data> companion,
                Function<Id, Update<Exercised<Unit>>> createUpdate,
                CreatedEvent event) {
            if (!event.getTemplateId().getEntityName().equals(companion.TEMPLATE_ID.getEntityName()))
                return Stream.empty();
            Ct ct = companion.fromCreatedEvent(event);
            Update<Exercised<Unit>> update = createUpdate.apply(ct.id);
            return update.commands().stream();
        }
}
