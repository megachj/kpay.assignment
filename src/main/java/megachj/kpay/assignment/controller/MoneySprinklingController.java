package megachj.kpay.assignment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import megachj.kpay.assignment.service.MoneySprinklingService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/money-sprinkling/v1")
public class MoneySprinklingController {

    private final MoneySprinklingService moneySprinklingService;

    @GetMapping("/hello-world")
    public String helloWorld() {
        return "Hello World!";
    }

    @PostMapping("/money")
    public void registerMoneySprinkling() {

    }

    @PutMapping("/money")
    public void takeMoney() {

    }

    @GetMapping("/money")
    public void getMoneySprinklingInfo() {

    }
}
