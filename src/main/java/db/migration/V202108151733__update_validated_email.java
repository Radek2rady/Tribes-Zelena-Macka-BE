package db.migration;

import org.jooq.DSLContext;
import org.jooq.impl.SQLDataType;

public class V202108151733__update_validated_email extends BaseMigration {

  @Override
  public void migrate(DSLContext dslContext) {
    dslContext.alterTable("users")
        .addColumn("is_confirmed_email", SQLDataType.BOOLEAN
            .defaultValue(false)
            .nullable(false))
        .execute();
  }

}
