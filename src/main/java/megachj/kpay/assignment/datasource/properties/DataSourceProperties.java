package megachj.kpay.assignment.datasource.properties;

import lombok.Data;

@Data
public class DataSourceProperties {

    protected String jdbcUrl;

    protected String username;

    protected String password;

    protected String poolName;

    protected int maximumPoolSize;

    protected int minimumIdle;

    protected int maxLifetime;

    protected int connectionTimeout;

    protected int idleTimeout;
}
