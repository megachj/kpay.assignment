package megachj.kpay.assignment.datasource.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import megachj.kpay.assignment.datasource.properties.DataSourceProperties;
import megachj.kpay.assignment.datasource.properties.MasterDataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement
@Configuration
public class DataSourceConfiguration {

    @Bean(name= "DataSource")
    public DataSource masterDataSource(MasterDataSourceProperties masterDataSourceProperties) {
        return createHikariDataSource(masterDataSourceProperties);
    }

    private HikariDataSource createHikariDataSource(DataSourceProperties properties) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(properties.getJdbcUrl());
        hikariConfig.setUsername(properties.getUsername());
        hikariConfig.setPassword(properties.getPassword());
        hikariConfig.setPoolName(properties.getPoolName());
        hikariConfig.setMaximumPoolSize(properties.getMaximumPoolSize());
        hikariConfig.setMinimumIdle(properties.getMinimumIdle());
        hikariConfig.setConnectionTimeout(properties.getConnectionTimeout());
        hikariConfig.setIdleTimeout(properties.getIdleTimeout());

        return new HikariDataSource(hikariConfig);
    }
}
