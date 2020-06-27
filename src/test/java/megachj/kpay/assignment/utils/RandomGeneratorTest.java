package megachj.kpay.assignment.utils;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class RandomGeneratorTest {

    @Test
    public void randomString_test() {
        String token = RandomGenerator.randomString(3);
        System.out.println(token);

        assertThat(token.length(), is(3));
    }

    @Test
    public void randomDistribution_test() {
        int value = 10_000_000;
        int n = 17;
        List<Integer> list = RandomGenerator.randomDistribution(value, n);
        System.out.println(list);

        assertThat(list.size(), is(n));
        assertThat(list.stream().mapToInt(v -> v).sum(), is(value));
        for (int i = 0; i < n; ++i)
            assertTrue(list.get(i) > 0);
    }
}
