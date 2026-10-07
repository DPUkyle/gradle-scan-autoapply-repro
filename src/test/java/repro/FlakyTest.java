package repro;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

class FlakyTest {

    /** Fails on the first attempt and passes on any retry (retries run in a new JVM, so state is kept in a file). */
    @Test
    void failsOnFirstAttemptOnly() throws IOException {
        Path counter = Paths.get(System.getProperty("flaky.attempt.file"));
        int attempt = Files.exists(counter) ? Integer.parseInt(Files.readString(counter).trim()) + 1 : 1;
        Files.writeString(counter, Integer.toString(attempt));
        System.out.println("FlakyTest attempt #" + attempt);
        if (attempt == 1) {
            fail("Simulated flaky failure on attempt 1; should be retried");
        }
    }
}
