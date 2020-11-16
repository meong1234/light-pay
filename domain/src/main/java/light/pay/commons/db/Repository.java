package light.pay.commons.db;

import com.gojek.ApplicationConfiguration;
import com.gojek.Figaro;
import light.pay.api.errors.Errors;
import light.pay.api.errors.Response;
import org.postgresql.ds.PGPoolingDataSource;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.logging.SLF4JLog;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Function;

public class Repository<T> {
    private DataSource source;
    private ApplicationConfiguration config;

    public Repository() {
        config = Figaro.configure(new HashSet<>(Arrays.asList(
                "DB_HOST",
                "DB_NAME",
                "DB_USERNAME",
                "DB_PASSWORD",
                "DB_POOL_SIZE",
                "JDBI_LOG_LEVEL"
        )));
        initializeConnectionPool(config);
    }

    protected DBI getDBI() {
        if (config == null) {
            config = Figaro.configure(new HashSet<>(Arrays.asList(
                    "DB_HOST",
                    "DB_NAME",
                    "DB_USERNAME",
                    "DB_PASSWORD",
                    "DB_POOL_SIZE",
                    "JDBI_LOG_LEVEL"
            )));
        }
        initializeConnectionPool(config);
        return new DBI(source);
    }

    private SLF4JLog.Level getJdbiLogLevel() {
        String configuredLevel = config.getValueAsString("JDBI_LOG_LEVEL");
        String level = configuredLevel.isEmpty() ? "TRACE" : configuredLevel;
        return SLF4JLog.Level.valueOf(level.toUpperCase());
    }

    protected T getDBInterface(Class<T> dBInterfaceClass) {
        DBI connection = getDBI();
        connection.setSQLLog(new SLF4JLog(LoggerFactory.getLogger(DBI.class), getJdbiLogLevel()));
        return connection.open(dBInterfaceClass);
    }

    protected <ReturnedType> ReturnedType withDBInterface(Class<T> dBInterfaceClass, Function<T, ReturnedType> sqlExecution) {
        T dbInterface = getDBInterface(dBInterfaceClass);
        try {
            return sqlExecution.apply(dbInterface);
        } finally {
            getDBI().close(dbInterface);
        }
    }

    protected <ReturnedType> Response<ReturnedType> execute(Class<T> dBInterfaceClass, Function<T, ReturnedType> sqlExecution) {
        try {
            ReturnedType executionResult = withDBInterface(dBInterfaceClass, sqlExecution);
            return Response.createSuccessResponse(executionResult);
        } catch (Exception e) {
            return Response.createErrorResponse(Errors.GENERIC_ERROR_CODE, "", e.getMessage());
        }
    }

    private void initializeConnectionPool(ApplicationConfiguration config) {
        if (source != null) return;

        String dbUsername = config.getValueAsString("DB_USERNAME");
        String dbPassword = config.getValueAsString("DB_PASSWORD");
        Integer dbPoolSize = config.getValueAsInt("DB_POOL_SIZE");
        Integer dbTimeout = config.getValueAsInt("DB_TIMEOUT", 1);
        Integer dbPortNumber = config.getValueAsInt("DB_PORT", 5432);

        source = getPgPoolingDataSource(config.getValueAsString("DB_HOST"), config.getValueAsString("DB_NAME"), dbUsername, dbPassword, dbPoolSize, dbTimeout, dbPortNumber);
    }


    private DataSource getPgPoolingDataSource(String dbHost, String dbName, String dbUsername, String dbPassword, Integer dbPoolSize, Integer dbTimeout, int dbPortNumber) {
        PGPoolingDataSource pgPoolingDataSource = new PGPoolingDataSource();
        pgPoolingDataSource.setServerName(dbHost);
        pgPoolingDataSource.setDatabaseName(dbName);
        pgPoolingDataSource.setUser(dbUsername);
        pgPoolingDataSource.setPassword(dbPassword);
        pgPoolingDataSource.setMaxConnections(dbPoolSize);
        pgPoolingDataSource.setPortNumber(dbPortNumber);
        pgPoolingDataSource.setSocketTimeout(dbTimeout);
        pgPoolingDataSource.setConnectTimeout(dbTimeout);

        return pgPoolingDataSource;
    }

    protected boolean isExists(Object entity) {
        return !Objects.isNull(entity);
    }
}
