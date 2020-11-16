package light.pay.commons.db;

import org.skife.jdbi.v2.Handle;

public class DatabaseUtils extends Repository<Object> {

    private static DatabaseUtils INSTANCE = new DatabaseUtils();

    public static void truncate(String tableName) {
        try(Handle handle = INSTANCE.getDBI().open()) {
            handle.execute("TRUNCATE TABLE " + tableName);
        }
    }
}