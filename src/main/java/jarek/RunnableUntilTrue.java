package jarek;

import java.util.function.Supplier;

public interface RunnableUntilTrue {
    void runUntilTrue(Supplier<Boolean> breakPredicate);
}
