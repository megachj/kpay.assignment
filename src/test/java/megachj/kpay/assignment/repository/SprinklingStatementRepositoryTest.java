package megachj.kpay.assignment.repository;

import megachj.kpay.assignment.model.entity.SprinklingStatementEntity;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("local")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SprinklingStatementRepositoryTest {

    @Autowired
    private SprinklingStatementRepository sprinklingStatementRepository;

    @Test(expected = DataIntegrityViolationException.class)
    public void duplicate_uniqueKey_test() {
        String token = "abc";

        SprinklingStatementEntity entity1 = SprinklingStatementEntity.newInstance(token, "room1", 1, 10000, 2, 10);
        sprinklingStatementRepository.save(entity1);

        SprinklingStatementEntity entity2 = SprinklingStatementEntity.newInstance(token, "room1", 2, 5000, 3, 10);
        sprinklingStatementRepository.save(entity2);
    }
}
