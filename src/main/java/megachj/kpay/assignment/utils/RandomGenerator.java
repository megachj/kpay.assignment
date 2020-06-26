package megachj.kpay.assignment.utils;

import java.util.ArrayList;
import java.util.List;

public class RandomGenerator {

    /**
     * value 를 n 개의 원소에 랜덤으로 분배하고 List 리턴한다.
     * List 는 아래의 조건을 만족한다.
     *  - 0 <= k < n 인 모든 원소 a[k] 는 1이상의 랜덤한 자연수
     *  - a[0] + a[1] + ... + a[n-1] = value
     *
     * @param value: 분배하고자 하는 총 값의 크기
     * @param n: 분배할 원소 크기
     * @return 크기가 n인 정수형 List
     */
    public static List<Integer> randomDistribution(int value, int n) throws IllegalArgumentException {
        if (n <= 0 || value < n) {
            throw new IllegalArgumentException();
        }

        List<Integer> result = new ArrayList<>(n);

        int remainValue = value;
        for (int i = 0; i < n-1; ++i) {
            int limitValue = (remainValue - (n-i-1)); // 현 단계에서 최대로 할당할 수 있는 limit 값
            int e = (int)(Math.log10(limitValue) + 1); // 현 단계 limit 값의 자릿수
            int randomValue = (int)((Math.random() * (int) Math.pow(10, e+1)) % limitValue);
            result.add(randomValue);
            remainValue -= randomValue;
        }
        result.add(remainValue);

        return result;
    }
}
