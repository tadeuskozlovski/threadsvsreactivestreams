package reactivestreams.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

@SpringBootTest
class TestApplicationTests {
    private static final long ELEMENT_COUNT = 99999;
    private static long beginClassicThreadsTest_timestamp;
    private static long beginReactiveStreamsTest_timestamp;
    private static List<Integer> listOfInt;
    private static final Logger log = Logger.getLogger(TestApplicationTests.class.getName());


    @BeforeAll
    static void init() {
        listOfInt = new ArrayList<>();

        log.info("Forming collection of data...");

        do {
            listOfInt.add(listOfInt.size());
        } while (listOfInt.size() < ELEMENT_COUNT);

        log.info("Data is formed.");
    }

    @Test
    void classicThreadsTest() {
        Consumer<Integer> intConsumer = i -> {
            new Thread() {
                public void run() {
                    if (i == 0) {
                        beginClassicThreadsTest_timestamp = Calendar.getInstance().getTimeInMillis();
                    }

                    String temp = listOfInt.get(i).toString();

                    if (i == ELEMENT_COUNT - 1) {
                        log.info(String.format(
                            "Test: %s | Exec time(milliseconds): %s | Last element value: %s",
                                this.getClass().getEnclosingMethod().getName(),
                                (Calendar.getInstance().getTimeInMillis() - beginClassicThreadsTest_timestamp),
                                temp
                            ));
                    }
                }
            }.start();
        };

        listOfInt.stream().forEach(intConsumer);
    }

    @Test
    void reactiveStreamsTest() {

        Flux.fromIterable(listOfInt).subscribe(i -> {
            if (i == 0) {
                beginReactiveStreamsTest_timestamp = Calendar.getInstance().getTimeInMillis();
            }

            String temp = listOfInt.get(i).toString();

            if (i == ELEMENT_COUNT - 1) {
                log.info(String.format(
                    "Test: %s | Exec time(milliseconds): %s | Last element value: %s",
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    (Calendar.getInstance().getTimeInMillis() - beginReactiveStreamsTest_timestamp),
                    temp
                ));
            }
        });
    }
}
