package org.example.scoutingsys.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAsyncExceptionHandler.class);

    public record CapturedAsyncError(String methodName, Throwable throwable) {}

    private final List<CapturedAsyncError> capturedErrors = new CopyOnWriteArrayList<>();

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... params) {
        log.error("Async uncaught exception in method: {}() - Error: {}",
                method.getName(), throwable.getMessage(), throwable);
        capturedErrors.add(new CapturedAsyncError(method.getName(), throwable));
    }

    public List<CapturedAsyncError> getCapturedErrors() {
        return Collections.unmodifiableList(capturedErrors);
    }

    public void clearCapturedErrors() {
        capturedErrors.clear();
    }
}
