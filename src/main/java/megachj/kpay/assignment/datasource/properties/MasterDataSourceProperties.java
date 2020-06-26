package megachj.kpay.assignment.datasource.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Component
@ConfigurationProperties(value="kpay-datasource.master")
public class MasterDataSourceProperties extends DataSourceProperties {
}
