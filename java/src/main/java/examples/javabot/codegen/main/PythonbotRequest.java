package examples.javabot.codegen.main;

import com.daml.ledger.javaapi.data.ContractFilter;
import com.daml.ledger.javaapi.data.CreateAndExerciseCommand;
import com.daml.ledger.javaapi.data.CreateCommand;
import com.daml.ledger.javaapi.data.CreatedEvent;
import com.daml.ledger.javaapi.data.DamlRecord;
import com.daml.ledger.javaapi.data.ExerciseCommand;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.Party;
import com.daml.ledger.javaapi.data.Template;
import com.daml.ledger.javaapi.data.Text;
import com.daml.ledger.javaapi.data.Unit;
import com.daml.ledger.javaapi.data.Value;
import com.daml.ledger.javaapi.data.codegen.Choice;
import com.daml.ledger.javaapi.data.codegen.ContractCompanion;
import com.daml.ledger.javaapi.data.codegen.ContractTypeCompanion;
import com.daml.ledger.javaapi.data.codegen.Created;
import com.daml.ledger.javaapi.data.codegen.Exercised;
import com.daml.ledger.javaapi.data.codegen.PrimitiveValueDecoders;
import com.daml.ledger.javaapi.data.codegen.Update;
import com.daml.ledger.javaapi.data.codegen.ValueDecoder;
import java.lang.Deprecated;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class PythonbotRequest extends Template {
  public static final Identifier TEMPLATE_ID = new Identifier("df8b295b9fd9aa17cdd586d85b456649a31323ebcad45a7ddef6885e90e8b2d7", "Main", "PythonbotRequest");

  public static final Choice<PythonbotRequest, examples.javabot.codegen.da.internal.template.Archive, Unit> CHOICE_Archive = 
      Choice.create("Archive", value$ -> value$.toValue(), value$ ->
        examples.javabot.codegen.da.internal.template.Archive.valueDecoder().decode(value$),
        value$ -> PrimitiveValueDecoders.fromUnit.decode(value$));

  public static final Choice<PythonbotRequest, Pythonbot_Accept, PythonbotResponse.ContractId> CHOICE_Pythonbot_Accept = 
      Choice.create("Pythonbot_Accept", value$ -> value$.toValue(), value$ ->
        Pythonbot_Accept.valueDecoder().decode(value$), value$ ->
        new PythonbotResponse.ContractId(value$.asContractId().orElseThrow(() -> new IllegalArgumentException("Expected value$ to be of type com.daml.ledger.javaapi.data.ContractId")).getValue()));

  public static final ContractCompanion.WithoutKey<Contract, ContractId, PythonbotRequest> COMPANION = 
      new ContractCompanion.WithoutKey<>("examples.javabot.codegen.main.PythonbotRequest",
        TEMPLATE_ID, ContractId::new, v -> PythonbotRequest.templateValueDecoder().decode(v),
        Contract::new, List.of(CHOICE_Archive, CHOICE_Pythonbot_Accept));

  public final String party;

  public final String correlationId;

  public PythonbotRequest(String party, String correlationId) {
    this.party = party;
    this.correlationId = correlationId;
  }

  @Override
  public Update<Created<ContractId>> create() {
    return new Update.CreateUpdate<ContractId, Created<ContractId>>(new CreateCommand(PythonbotRequest.TEMPLATE_ID, this.toValue()), x -> x, ContractId::new);
  }

  /**
   * @deprecated since Daml 2.3.0; use {@code createAnd().exerciseArchive} instead
   */
  @Deprecated
  public Update<Exercised<Unit>> createAndExerciseArchive(
      examples.javabot.codegen.da.internal.template.Archive arg) {
    return createAnd().exerciseArchive(arg);
  }

  /**
   * @deprecated since Daml 2.3.0; use {@code createAnd().exerciseArchive} instead
   */
  @Deprecated
  public Update<Exercised<Unit>> createAndExerciseArchive() {
    return createAndExerciseArchive(new examples.javabot.codegen.da.internal.template.Archive());
  }

  /**
   * @deprecated since Daml 2.3.0; use {@code createAnd().exercisePythonbot_Accept} instead
   */
  @Deprecated
  public Update<Exercised<PythonbotResponse.ContractId>> createAndExercisePythonbot_Accept(
      Pythonbot_Accept arg) {
    return createAnd().exercisePythonbot_Accept(arg);
  }

  /**
   * @deprecated since Daml 2.3.0; use {@code createAnd().exercisePythonbot_Accept} instead
   */
  @Deprecated
  public Update<Exercised<PythonbotResponse.ContractId>> createAndExercisePythonbot_Accept() {
    return createAndExercisePythonbot_Accept(new Pythonbot_Accept());
  }

  public static Update<Created<ContractId>> create(String party, String correlationId) {
    return new PythonbotRequest(party, correlationId).create();
  }

  @Override
  public CreateAnd createAnd() {
    return new CreateAnd(this);
  }

  @Override
  protected ContractCompanion.WithoutKey<Contract, ContractId, PythonbotRequest> getCompanion() {
    return COMPANION;
  }

  /**
   * @deprecated since Daml 2.5.0; use {@code valueDecoder} instead
   */
  @Deprecated
  public static PythonbotRequest fromValue(Value value$) throws IllegalArgumentException {
    return valueDecoder().decode(value$);
  }

  public static ValueDecoder<PythonbotRequest> valueDecoder() throws IllegalArgumentException {
    return ContractCompanion.valueDecoder(COMPANION);
  }

  public DamlRecord toValue() {
    ArrayList<DamlRecord.Field> fields = new ArrayList<DamlRecord.Field>(2);
    fields.add(new DamlRecord.Field("party", new Party(this.party)));
    fields.add(new DamlRecord.Field("correlationId", new Text(this.correlationId)));
    return new DamlRecord(fields);
  }

  private static ValueDecoder<PythonbotRequest> templateValueDecoder() throws
      IllegalArgumentException {
    return value$ -> {
      Value recordValue$ = value$;
      List<DamlRecord.Field> fields$ = PrimitiveValueDecoders.recordCheck(2, recordValue$);
      String party = PrimitiveValueDecoders.fromParty.decode(fields$.get(0).getValue());
      String correlationId = PrimitiveValueDecoders.fromText.decode(fields$.get(1).getValue());
      return new PythonbotRequest(party, correlationId);
    } ;
  }

  public static ContractFilter<Contract> contractFilter() {
    return ContractFilter.of(COMPANION);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null) {
      return false;
    }
    if (!(object instanceof PythonbotRequest)) {
      return false;
    }
    PythonbotRequest other = (PythonbotRequest) object;
    return Objects.equals(this.party, other.party) &&
        Objects.equals(this.correlationId, other.correlationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.party, this.correlationId);
  }

  @Override
  public String toString() {
    return String.format("examples.javabot.codegen.main.PythonbotRequest(%s, %s)", this.party,
        this.correlationId);
  }

  public static final class ContractId extends com.daml.ledger.javaapi.data.codegen.ContractId<PythonbotRequest> implements Exercises<ExerciseCommand> {
    public ContractId(String contractId) {
      super(contractId);
    }

    @Override
    protected ContractTypeCompanion<? extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, ?>, ContractId, PythonbotRequest, ?> getCompanion(
        ) {
      return COMPANION;
    }

    public static ContractId fromContractId(
        com.daml.ledger.javaapi.data.codegen.ContractId<PythonbotRequest> contractId) {
      return COMPANION.toContractId(contractId);
    }
  }

  public static class Contract extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, PythonbotRequest> {
    public Contract(ContractId id, PythonbotRequest data, Optional<String> agreementText,
        Set<String> signatories, Set<String> observers) {
      super(id, data, agreementText, signatories, observers);
    }

    @Override
    protected ContractCompanion<Contract, ContractId, PythonbotRequest> getCompanion() {
      return COMPANION;
    }

    public static Contract fromIdAndRecord(String contractId, DamlRecord record$,
        Optional<String> agreementText, Set<String> signatories, Set<String> observers) {
      return COMPANION.fromIdAndRecord(contractId, record$, agreementText, signatories, observers);
    }

    public static Contract fromCreatedEvent(CreatedEvent event) {
      return COMPANION.fromCreatedEvent(event);
    }
  }

  public interface Exercises<Cmd> extends com.daml.ledger.javaapi.data.codegen.Exercises.Archive<Cmd> {
    default Update<Exercised<Unit>> exerciseArchive(
        examples.javabot.codegen.da.internal.template.Archive arg) {
      return makeExerciseCmd(CHOICE_Archive, arg);
    }

    default Update<Exercised<Unit>> exerciseArchive() {
      return exerciseArchive(new examples.javabot.codegen.da.internal.template.Archive());
    }

    default Update<Exercised<PythonbotResponse.ContractId>> exercisePythonbot_Accept(
        Pythonbot_Accept arg) {
      return makeExerciseCmd(CHOICE_Pythonbot_Accept, arg);
    }

    default Update<Exercised<PythonbotResponse.ContractId>> exercisePythonbot_Accept() {
      return exercisePythonbot_Accept(new Pythonbot_Accept());
    }
  }

  public static final class CreateAnd extends com.daml.ledger.javaapi.data.codegen.CreateAnd implements Exercises<CreateAndExerciseCommand> {
    CreateAnd(Template createArguments) {
      super(createArguments);
    }

    @Override
    protected ContractTypeCompanion<? extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, ?>, ContractId, PythonbotRequest, ?> getCompanion(
        ) {
      return COMPANION;
    }
  }
}
