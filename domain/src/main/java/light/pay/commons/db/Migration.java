package light.pay.commons.db;

import com.gojek.ApplicationConfiguration;
import com.gojek.Figaro;
import org.flywaydb.core.Flyway;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Migration {
    public static void main(String[] args) {

        Set<String> requiredConfig = new HashSet<>(Arrays.asList(
                "DB_HOST",
                "DB_NAME",
                "DB_USERNAME",
                "DB_PASSWORD"
        ));
        ApplicationConfiguration config = Figaro.configure(requiredConfig);

        Flyway flyway = new Flyway();
        flyway.setDataSource(
                String.format("jdbc:postgresql://%s/%s", config.getValueAsString("DB_HOST"), config.getValueAsString("DB_NAME")),
                config.getValueAsString("DB_USERNAME"),
                config.getValueAsString("DB_PASSWORD"));
        flyway.setBaselineOnMigrate(true);
        flyway.migrate();
    }
}
