package db.migration;

import org.jooq.DSLContext;
import org.jooq.impl.SQLDataType;

public class V202108122116__add_is_defeated_to_kingdoms extends BaseMigration {

  @Override
  public void migrate(DSLContext dslContext) {
    dslContext.alterTable("kingdoms")
        .addColumn("is_defeated", SQLDataType.BOOLEAN
            .defaultValue(false)
            .nullable(false))
        .execute();
  }

}

