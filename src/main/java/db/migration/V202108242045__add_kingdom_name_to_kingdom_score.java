package db.migration;

import org.jooq.DSLContext;
import org.jooq.impl.SQLDataType;

public class V202108242045__add_kingdom_name_to_kingdom_score extends BaseMigration {

  @Override
  public void migrate(DSLContext dslContext) {
    dslContext.alterTable("kingdom_score")
        .addColumn("kingdom_name", SQLDataType.VARCHAR(255)
            .defaultValue("")
            .nullable(false))
        .execute();
  }
}


