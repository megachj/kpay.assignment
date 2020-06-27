package megachj.kpay.assignment.controller;

import megachj.kpay.assignment.service.MoneySprinklingService;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("local")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MoneySprinklingControllerTest {

    @Autowired
    private MoneySprinklingController moneySprinklingController;

    @Autowired
    private MoneySprinklingService moneySprinklingService;

    private MockMvc mockMvc;

    @Before
    public void initMockMvc() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);

        mockMvc = MockMvcBuilders.standaloneSetup(moneySprinklingController)
                .setHandlerExceptionResolvers(createExceptionResolver())
                .addFilter(filter).build();
    }

    private ExceptionHandlerExceptionResolver createExceptionResolver() {
        ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
            @Override
            protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
                Method method = new ExceptionHandlerMethodResolver(RestProcessingExceptionHandler.class).resolveMethod(exception);
                return new ServletInvocableHandlerMethod(new RestProcessingExceptionHandler(), method);
            }
        };
        exceptionResolver.afterPropertiesSet();
        exceptionResolver.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        return exceptionResolver;
    }

    @Test
    public void registerMoneySprinkling_test() throws Exception {
        int userId = 1;
        String roomId = "room1";
        int amount = 10000;
        int distributedNumber = 2;

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/money-sprinkling/v1.0/money"
                        + "?amount={amount}"
                        + "&distributedNumber={distributedNumber}"
                , amount
                , distributedNumber
        )
                .header("X-USER-ID", userId)
                .header("X-ROOM-ID", roomId)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("header.resultCode", is(0)));
    }

    @Test
    public void receiveMoney_test() throws Exception {
        // user1 이 room1 에 2명에게 10,000원 뿌리기
        String token = moneySprinklingService.addMoneySprinkling(1, "room1", 10000, 2);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/money-sprinkling/v1.0/money/receive"
                        + "?token={token}"
                , token
        )
                .header("X-USER-ID", 2)
                .header("X-ROOM-ID", "room1")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("header.resultCode", is(0)));
    }

    @Test
    public void getSprinklingInfo_test() throws Exception {
        // user1 이 room1 에 2명에게 10,000원 뿌리기
        String token = moneySprinklingService.addMoneySprinkling(1, "room1", 10000, 2);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/money-sprinkling/v1.0/sprinkling-info"
                        + "?token={token}"
                , token
        )
                .header("X-USER-ID", 1)
                .header("X-ROOM-ID", "room1")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("header.resultCode", is(0)));
    }
}
