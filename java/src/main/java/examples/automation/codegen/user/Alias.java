package examples.automation.codegen.user;

import static com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoders.apply;

import com.daml.ledger.javaapi.data.ContractFilter;
import com.daml.ledger.javaapi.data.CreateAndExerciseCommand;
import com.daml.ledger.javaapi.data.CreateCommand;
import com.daml.ledger.javaapi.data.CreatedEvent;
import com.daml.ledger.javaapi.data.DamlRecord;
import com.daml.ledger.javaapi.data.ExerciseByKeyCommand;
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
import com.daml.ledger.javaapi.data.codegen.ContractWithKey;
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
import examples.automation.codegen.da.types.Tuple2;
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

public final class Alias extends Template {
  public static final Identifier TEMPLATE_ID = new Identifier("70aa5450d67bf911d8683d255227ca8fd0ca0feb4d2fcaa4751cd39d13ce476c", "User", "Alias");

  public static final Choice<Alias, Change, ContractId> CHOICE_Change = 
      Choice.create("Change", value$ -> value$.toValue(), value$ -> Change.valueDecoder()
        .decode(value$), value$ ->
        new ContractId(value$.asContractId().orElseThrow(() -> new IllegalArgumentException("Expected value$ to be of type com.daml.ledger.javaapi.data.ContractId")).getValue()));

  public static final Choice<Alias, examples.automation.codegen.da.internal.template.Archive, Unit> CHOICE_Archive = 
      Choice.create("Archive", value$ -> value$.toValue(), value$ ->
        examples.automation.codegen.da.internal.template.Archive.valueDecoder().decode(value$),
        value$ -> PrimitiveValueDecoders.fromUnit.decode(value$));

  public static final ContractCompanion.WithKey<Contract, ContractId, Alias, Tuple2<String, String>> COMPANION = 
      new ContractCompanion.WithKey<>("examples.automation.codegen.user.Alias", TEMPLATE_ID,
        ContractId::new, v -> Alias.templateValueDecoder().decode(v), Alias::fromJson,
        Contract::new, List.of(CHOICE_Change, CHOICE_Archive), e -> Tuple2.<java.lang.String,
        java.lang.String>valueDecoder(PrimitiveValueDecoders.fromParty,
        PrimitiveValueDecoders.fromParty).decode(e));

  public final String username;

  public final String alias;

  public final String public$;

  public Alias(String username, String alias, String public$) {
    this.username = username;
    this.alias = alias;
    this.public$ = public$;
  }

  @Override
  public Update<Created<ContractId>> create() {
    return new Update.CreateUpdate<ContractId, Created<ContractId>>(new CreateCommand(Alias.TEMPLATE_ID, this.toValue()), x -> x, ContractId::new);
  }

  /**
   * @deprecated since Daml 2.3.0; use {@code byKey(key).exerciseChange} instead
   */
  @Deprecated
  public static Update<Exercised<ContractId>> exerciseByKeyChange(Tuple2<String, String> key,
      Change arg) {
    return byKey(key).exerciseChange(arg);
  }

  /**
   * @deprecated since Daml 2.3.0; use {@code byKey(key).exerciseChange(newAlias)} instead
   */
  @Deprecated
  public static Update<Exercised<ContractId>> exerciseByKeyChange(Tuple2<String, String> key,
      String newAlias) {
    return byKey(key).exerciseChange(newAlias);
  }

  /**
   * @deprecated since Daml 2.3.0; use {@code byKey(key).exerciseArchive} instead
   */
  @Deprecated
  public static Update<Exercised<Unit>> exerciseByKeyArchive(Tuple2<String, String> key,
      examples.automation.codegen.da.internal.template.Archive arg) {
    return byKey(key).exerciseArchive(arg);
  }

  /**
   * @deprecated since Daml 2.3.0; use {@code byKey(key).exerciseArchive()} instead
   */
  @Deprecated
  public static Update<Exercised<Unit>> exerciseByKeyArchive(Tuple2<String, String> key) {
    return byKey(key).exerciseArchive();
  }

  /**
   * @deprecated since Daml 2.3.0; use {@code createAnd().exerciseChange} instead
   */
  @Deprecated
  public Update<Exercised<ContractId>> createAndExerciseChange(Change arg) {
    return createAnd().exerciseChange(arg);
  }

  /**
   * @deprecated since Daml 2.3.0; use {@code createAnd().exerciseChange} instead
   */
  @Deprecated
  public Update<Exercised<ContractId>> createAndExerciseChange(String newAlias) {
    return createAndExerciseChange(new Change(newAlias));
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

  public static Update<Created<ContractId>> create(String username, String alias, String public$) {
    return new Alias(username, alias, public$).create();
  }

  @Override
  public CreateAnd createAnd() {
    return new CreateAnd(this);
  }

  @Override
  protected ContractCompanion.WithKey<Contract, ContractId, Alias, Tuple2<String, String>> getCompanion(
      ) {
    return COMPANION;
  }

  /**
   * @deprecated since Daml 2.5.0; use {@code valueDecoder} instead
   */
  @Deprecated
  public static Alias fromValue(Value value$) throws IllegalArgumentException {
    return valueDecoder().decode(value$);
  }

  public static ValueDecoder<Alias> valueDecoder() throws IllegalArgumentException {
    return ContractCompanion.valueDecoder(COMPANION);
  }

  public DamlRecord toValue() {
    ArrayList<DamlRecord.Field> fields = new ArrayList<DamlRecord.Field>(3);
    fields.add(new DamlRecord.Field("username", new Party(this.username)));
    fields.add(new DamlRecord.Field("alias", new Text(this.alias)));
    fields.add(new DamlRecord.Field("public", new Party(this.public$)));
    return new DamlRecord(fields);
  }

  private static ValueDecoder<Alias> templateValueDecoder() throws IllegalArgumentException {
    return value$ -> {
      Value recordValue$ = value$;
      List<DamlRecord.Field> fields$ = PrimitiveValueDecoders.recordCheck(3,0, recordValue$);
      String username = PrimitiveValueDecoders.fromParty.decode(fields$.get(0).getValue());
      String alias = PrimitiveValueDecoders.fromText.decode(fields$.get(1).getValue());
      String public$ = PrimitiveValueDecoders.fromParty.decode(fields$.get(2).getValue());
      return new Alias(username, alias, public$);
    } ;
  }

  public static JsonLfDecoder<Alias> jsonDecoder() {
    return JsonLfDecoders.record(Arrays.asList("username", "alias", "public$"), name -> {
          switch (name) {
            case "username": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(0, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.party);
            case "alias": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(1, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.text);
            case "public$": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(2, com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.party);
            default: return null;
          }
        }
        , (Object[] args) -> new Alias(JsonLfDecoders.cast(args[0]), JsonLfDecoders.cast(args[1]), JsonLfDecoders.cast(args[2])));
  }

  public static Alias fromJson(String json) throws JsonLfDecoder.Error {
    return jsonDecoder().decode(new JsonLfReader(json));
  }

  public JsonLfEncoder jsonEncoder() {
    return JsonLfEncoders.record(
        JsonLfEncoders.Field.of("username", apply(JsonLfEncoders::party, username)),
        JsonLfEncoders.Field.of("alias", apply(JsonLfEncoders::text, alias)),
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
    if (!(object instanceof Alias)) {
      return false;
    }
    Alias other = (Alias) object;
    return Objects.equals(this.username, other.username) &&
        Objects.equals(this.alias, other.alias) && Objects.equals(this.public$, other.public$);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.username, this.alias, this.public$);
  }

  @Override
  public String toString() {
    return String.format("examples.automation.codegen.user.Alias(%s, %s, %s)", this.username,
        this.alias, this.public$);
  }

  /**
   * Set up an {@link ExerciseByKeyCommand}; invoke an {@code exercise} method on the result of
      this to finish creating the command, or convert to an interface first with {@code toInterface}
      to invoke an interface {@code exercise} method.
   */
  public static ByKey byKey(Tuple2<String, String> key) {
    return new ByKey(key.toValue(v$0 -> new Party(v$0), v$1 -> new Party(v$1)));
  }

  public static final class ContractId extends com.daml.ledger.javaapi.data.codegen.ContractId<Alias> implements Exercises<ExerciseCommand> {
    public ContractId(String contractId) {
      super(contractId);
    }

    @Override
    protected ContractTypeCompanion<? extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, ?>, ContractId, Alias, ?> getCompanion(
        ) {
      return COMPANION;
    }

    public static ContractId fromContractId(
        com.daml.ledger.javaapi.data.codegen.ContractId<Alias> contractId) {
      return COMPANION.toContractId(contractId);
    }
  }

  public static class Contract extends ContractWithKey<ContractId, Alias, Tuple2<String, String>> {
    public Contract(ContractId id, Alias data, Optional<String> agreementText,
        Optional<Tuple2<String, String>> key, Set<String> signatories, Set<String> observers) {
      super(id, data, agreementText, key, signatories, observers);
    }

    @Override
    protected ContractCompanion<Contract, ContractId, Alias> getCompanion() {
      return COMPANION;
    }

    public static Contract fromIdAndRecord(String contractId, DamlRecord record$,
        Optional<String> agreementText, Optional<Tuple2<String, String>> key,
        Set<String> signatories, Set<String> observers) {
      return COMPANION.fromIdAndRecord(contractId, record$, agreementText, key, signatories,
          observers);
    }

    public static Contract fromCreatedEvent(CreatedEvent event) {
      return COMPANION.fromCreatedEvent(event);
    }
  }

  public interface Exercises<Cmd> extends com.daml.ledger.javaapi.data.codegen.Exercises.Archive<Cmd> {
    default Update<Exercised<ContractId>> exerciseChange(Change arg) {
      return makeExerciseCmd(CHOICE_Change, arg);
    }

    default Update<Exercised<ContractId>> exerciseChange(String newAlias) {
      return exerciseChange(new Change(newAlias));
    }

    default Update<Exercised<Unit>> exerciseArchive(
        examples.automation.codegen.da.internal.template.Archive arg) {
      return makeExerciseCmd(CHOICE_Archive, arg);
    }

    default Update<Exercised<Unit>> exerciseArchive() {
      return exerciseArchive(new examples.automation.codegen.da.internal.template.Archive());
    }
  }

  public static final class CreateAnd extends com.daml.ledger.javaapi.data.codegen.CreateAnd implements Exercises<CreateAndExerciseCommand> {
    CreateAnd(Template createArguments) {
      super(createArguments);
    }

    @Override
    protected ContractTypeCompanion<? extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, ?>, ContractId, Alias, ?> getCompanion(
        ) {
      return COMPANION;
    }
  }

  public static final class ByKey extends com.daml.ledger.javaapi.data.codegen.ByKey implements Exercises<ExerciseByKeyCommand> {
    ByKey(Value key) {
      super(key);
    }

    @Override
    protected ContractTypeCompanion<? extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, ?>, ContractId, Alias, ?> getCompanion(
        ) {
      return COMPANION;
    }
  }
}
