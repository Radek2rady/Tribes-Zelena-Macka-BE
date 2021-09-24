package db.migration;

import org.jooq.DSLContext;
import org.jooq.impl.SQLDataType;

public class V202108151735__update_confirmation_token extends BaseMigration {

  @Override
  public void migrate(DSLContext dslContext) {
    dslContext.alterTable("users")
        .addColumn("token", SQLDataType.VARCHAR(100)
            .defaultValue("")
            .nullable(false))
        .execute();
  }
}
