package examples.automation.codegen.da.types;

import static com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoders.apply;

import com.daml.ledger.javaapi.data.DamlRecord;
import com.daml.ledger.javaapi.data.Value;
import com.daml.ledger.javaapi.data.codegen.PrimitiveValueDecoders;
import com.daml.ledger.javaapi.data.codegen.ValueDecoder;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoder;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoder;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfEncoders;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfReader;
import com.daml.ledger.javaapi.data.codegen.json.JsonLfWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.Deprecated;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class Tuple2<t1, t2> {
  public static final String _packageId = "40f452260bef3f29dede136108fc08a88d5a5250310281067087da6f0baddff7";

  public final t1 _1;

  public final t2 _2;

  public Tuple2(t1 _1, t2 _2) {
    this._1 = _1;
    this._2 = _2;
  }

  /**
   * @deprecated since Daml 2.5.0; use {@code valueDecoder} instead
   */
  @Deprecated
  public static <t1, t2> Tuple2<t1, t2> fromValue(Value value$, Function<Value, t1> fromValuet1,
      Function<Value, t2> fromValuet2) throws IllegalArgumentException {
    return Tuple2.<t1, t2>valueDecoder(ValueDecoder.fromFunction(fromValuet1),
          ValueDecoder.fromFunction(fromValuet2)).decode(value$);
  }

  public static <t1, t2> ValueDecoder<Tuple2<t1, t2>> valueDecoder(ValueDecoder<t1> fromValuet1,
      ValueDecoder<t2> fromValuet2) throws IllegalArgumentException {
    return value$ -> {
      Value recordValue$ = value$;
      List<DamlRecord.Field> fields$ = PrimitiveValueDecoders.recordCheck(2,0, recordValue$);
      t1 _1 = fromValuet1.decode(fields$.get(0).getValue());
      t2 _2 = fromValuet2.decode(fields$.get(1).getValue());
      return new Tuple2<t1, t2>(_1, _2);
    } ;
  }

  public DamlRecord toValue(Function<t1, Value> toValuet1, Function<t2, Value> toValuet2) {
    ArrayList<DamlRecord.Field> fields = new ArrayList<DamlRecord.Field>(2);
    fields.add(new DamlRecord.Field("_1", toValuet1.apply(this._1)));
    fields.add(new DamlRecord.Field("_2", toValuet2.apply(this._2)));
    return new DamlRecord(fields);
  }

  public static <t1, t2> JsonLfDecoder<Tuple2<t1, t2>> jsonDecoder(JsonLfDecoder<t1> decodet1,
      JsonLfDecoder<t2> decodet2) {
    return JsonLfDecoders.record(Arrays.asList("_1", "_2"), name -> {
          switch (name) {
            case "_1": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(0, decodet1);
            case "_2": return com.daml.ledger.javaapi.data.codegen.json.JsonLfDecoders.JavaArg.at(1, decodet2);
            default: return null;
          }
        }
        , (Object[] args) -> new Tuple2<t1, t2>(JsonLfDecoders.cast(args[0]), JsonLfDecoders.cast(args[1])));
  }

  public static <t1, t2> Tuple2<t1, t2> fromJson(String json, JsonLfDecoder<t1> decodet1,
      JsonLfDecoder<t2> decodet2) throws JsonLfDecoder.Error {
    return jsonDecoder(decodet1, decodet2).decode(new JsonLfReader(json));
  }

  public JsonLfEncoder jsonEncoder(Function<t1, JsonLfEncoder> makeEncoder_t1,
      Function<t2, JsonLfEncoder> makeEncoder_t2) {
    return JsonLfEncoders.record(JsonLfEncoders.Field.of("_1", apply(makeEncoder_t1, _1)),
        JsonLfEncoders.Field.of("_2", apply(makeEncoder_t2, _2)));
  }

  public String toJson(Function<t1, JsonLfEncoder> makeEncoder_t1,
      Function<t2, JsonLfEncoder> makeEncoder_t2) {
    var w = new StringWriter();
    try {
      this.jsonEncoder(makeEncoder_t1, makeEncoder_t2).encode(new JsonLfWriter(w));
    } catch (IOException e) {
      // Not expected with StringWriter
      throw new UncheckedIOException(e);
    }
    return w.toString();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null) {
      return false;
    }
    if (!(object instanceof Tuple2<?, ?>)) {
      return false;
    }
    Tuple2<?, ?> other = (Tuple2<?, ?>) object;
    return Objects.equals(this._1, other._1) && Objects.equals(this._2, other._2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this._1, this._2);
  }

  @Override
  public String toString() {
    return String.format("examples.automation.codegen.da.types.Tuple2(%s, %s)", this._1, this._2);
  }
}
