package megachj.kpay.assignment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import megachj.kpay.assignment.repository.MoneySprinklingRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MoneySprinklingService {

    private final MoneySprinklingRepository moneySprinklingRepository;


}
