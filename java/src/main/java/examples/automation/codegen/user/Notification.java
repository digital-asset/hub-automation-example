package examples.automation.codegen.user;

import static com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoders.apply;

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
import com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoder;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoder;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoders;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfReader;
import java.lang.Deprecated;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Notification extends Template {
  public static final Identifier TEMPLATE_ID = new Identifier("7d485a05d84710d07b95eae92c79960d5398c61c34aff7d38896ad552d885a9c", "User", "Notification");

  public static final Choice<Notification, examples.automation.codegen.da.internal.template.Archive, Unit> CHOICE_Archive = 
      Choice.create("Archive", value$ -> value$.toValue(), value$ ->
        examples.automation.codegen.da.internal.template.Archive.valueDecoder().decode(value$),
        value$ -> PrimitiveValueDecoders.fromUnit.decode(value$));

  public static final Choice<Notification, Acknowledge, Unit> CHOICE_Acknowledge = 
      Choice.create("Acknowledge", value$ -> value$.toValue(), value$ -> Acknowledge.valueDecoder()
        .decode(value$), value$ -> PrimitiveValueDecoders.fromUnit.decode(value$));

  public static final ContractCompanion.WithoutKey<Contract, ContractId, Notification> COMPANION = 
      new ContractCompanion.WithoutKey<>("examples.automation.codegen.user.Notification",
        TEMPLATE_ID, ContractId::new, v -> Notification.templateValueDecoder().decode(v),
        Notification::fromJson, Contract::new, List.of(CHOICE_Archive, CHOICE_Acknowledge));

  public final String username;

  public final String message;

  public final String public$;

  public Notification(String username, String message, String public$) {
    this.username = username;
    this.message = message;
    this.public$ = public$;
  }

  @Override
  public Update<Created<ContractId>> create() {
    return new Update.CreateUpdate<ContractId, Created<ContractId>>(new CreateCommand(Notification.TEMPLATE_ID, this.toValue()), x -> x, ContractId::new);
  }

  /**
   * @deprecated since Daml 2.3.0; use {@code createAnd().exerciseArchive} instead
   */
  @Deprecated
  public Update<Exercised<Unit>> createAndExerciseArchive(
      examples.automation.codegen.da.internal.template.Archive arg) {
    return createAnd().exerciseArchive(arg);
  }

  /**
   * @deprecated since Daml 2.3.0; use {@code createAnd().exerciseArchive} instead
   */
  @Deprecated
  public Update<Exercised<Unit>> createAndExerciseArchive() {
    return createAndExerciseArchive(new examples.automation.codegen.da.internal.template.Archive());
  }

  /**
   * @deprecated since Daml 2.3.0; use {@code createAnd().exerciseAcknowledge} instead
   */
  @Deprecated
  public Update<Exercised<Unit>> createAndExerciseAcknowledge(Acknowledge arg) {
    return createAnd().exerciseAcknowledge(arg);
  }

  /**
   * @deprecated since Daml 2.3.0; use {@code createAnd().exerciseAcknowledge} instead
   */
  @Deprecated
  public Update<Exercised<Unit>> createAndExerciseAcknowledge() {
    return createAndExerciseAcknowledge(new Acknowledge());
  }

  public static Update<Created<ContractId>> create(String username, String message,
      String public$) {
    return new Notification(username, message, public$).create();
  }

  @Override
  public CreateAnd createAnd() {
    return new CreateAnd(this);
  }

  @Override
  protected ContractCompanion.WithoutKey<Contract, ContractId, Notification> getCompanion() {
    return COMPANION;
  }

  /**
   * @deprecated since Daml 2.5.0; use {@code valueDecoder} instead
   */
  @Deprecated
  public static Notification fromValue(Value value$) throws IllegalArgumentException {
    return valueDecoder().decode(value$);
  }

  public static ValueDecoder<Notification> valueDecoder() throws IllegalArgumentException {
    return ContractCompanion.valueDecoder(COMPANION);
  }

  public DamlRecord toValue() {
    ArrayList<DamlRecord.Field> fields = new ArrayList<DamlRecord.Field>(3);
    fields.add(new DamlRecord.Field("username", new Party(this.username)));
    fields.add(new DamlRecord.Field("message", new Text(this.message)));
    fields.add(new DamlRecord.Field("public", new Party(this.public$)));
    return new DamlRecord(fields);
  }

  private static ValueDecoder<Notification> templateValueDecoder() throws IllegalArgumentException {
    return value$ -> {
      Value recordValue$ = value$;
      List<DamlRecord.Field> fields$ = PrimitiveValueDecoders.recordCheck(3,0, recordValue$);
      String username = PrimitiveValueDecoders.fromParty.decode(fields$.get(0).getValue());
      String message = PrimitiveValueDecoders.fromText.decode(fields$.get(1).getValue());
      String public$ = PrimitiveValueDecoders.fromParty.decode(fields$.get(2).getValue());
      return new Notification(username, message, public$);
    } ;
  }

  public static JsonLfDecoder<Notification> jsonDecoder() {
    return JsonLfDecoders.record(Arrays.asList("username", "message", "public$"), name -> {
          switch (name) {
            case "username": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(0, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.party);
            case "message": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(1, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.text);
            case "public$": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(2, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.party);
            default: return null;
          }
        }
        , (Object[] args) -> new Notification(JsonLfDecoders.cast(args[0]), JsonLfDecoders.cast(args[1]), JsonLfDecoders.cast(args[2])));
  }

  public static Notification fromJson(String json) throws JsonLfDecoder.Error {
    return jsonDecoder().decode(new JsonLfReader(json));
  }

  public JsonLfEncoder jsonEncoder() {
    return JsonLfEncoders.record(
        JsonLfEncoders.Field.of("username", apply(JsonLfEncoders::party, username)),
        JsonLfEncoders.Field.of("message", apply(JsonLfEncoders::text, message)),
        JsonLfEncoders.Field.of("public$", apply(JsonLfEncoders::party, public$)));
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
    if (!(object instanceof Notification)) {
      return false;
    }
    Notification other = (Notification) object;
    return Objects.equals(this.username, other.username) &&
        Objects.equals(this.message, other.message) && Objects.equals(this.public$, other.public$);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.username, this.message, this.public$);
  }

  @Override
  public String toString() {
    return String.format("examples.automation.codegen.user.Notification(%s, %s, %s)", this.username,
        this.message, this.public$);
  }

  public static final class ContractId extends com.daml.ledger.javaapi.data.codegen.ContractId<Notification> implements Exercises<ExerciseCommand> {
    public ContractId(String contractId) {
      super(contractId);
    }

    @Override
    protected ContractTypeCompanion<? extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, ?>, ContractId, Notification, ?> getCompanion(
        ) {
      return COMPANION;
    }

    public static ContractId fromContractId(
        com.daml.ledger.javaapi.data.codegen.ContractId<Notification> contractId) {
      return COMPANION.toContractId(contractId);
    }
  }

  public static class Contract extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, Notification> {
    public Contract(ContractId id, Notification data, Optional<String> agreementText,
        Set<String> signatories, Set<String> observers) {
      super(id, data, agreementText, signatories, observers);
    }

    @Override
    protected ContractCompanion<Contract, ContractId, Notification> getCompanion() {
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
        examples.automation.codegen.da.internal.template.Archive arg) {
      return makeExerciseCmd(CHOICE_Archive, arg);
    }

    default Update<Exercised<Unit>> exerciseArchive() {
      return exerciseArchive(new examples.automation.codegen.da.internal.template.Archive());
    }

    default Update<Exercised<Unit>> exerciseAcknowledge(Acknowledge arg) {
      return makeExerciseCmd(CHOICE_Acknowledge, arg);
    }

    default Update<Exercised<Unit>> exerciseAcknowledge() {
      return exerciseAcknowledge(new Acknowledge());
    }
  }

  public static final class CreateAnd extends com.daml.ledger.javaapi.data.codegen.CreateAnd implements Exercises<CreateAndExerciseCommand> {
    CreateAnd(Template createArguments) {
      super(createArguments);
    }

    @Override
    protected ContractTypeCompanion<? extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, ?>, ContractId, Notification, ?> getCompanion(
        ) {
      return COMPANION;
    }
  }
}
