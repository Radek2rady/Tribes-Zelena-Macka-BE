package db.migration;

import org.jooq.DSLContext;
import org.jooq.impl.SQLDataType;

public class V202108242036__add_is_history_to_kingdom_score extends BaseMigration {

  @Override
  public void migrate(DSLContext dslContext) {
    dslContext.alterTable("kingdom_score")
        .addColumn("is_history", SQLDataType.BOOLEAN
            .defaultValue(false)
            .nullable(false))
        .execute();
  }
}

