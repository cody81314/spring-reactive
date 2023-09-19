package com.example.demo.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.util.context.ContextView;

import java.time.Instant;
import java.util.function.Consumer;

import static com.example.demo.constant.AOPConst.POINTCUT_WEBLAYER;
import static com.example.demo.constant.LoggerConst.PATH_URI;
import static com.example.demo.constant.LoggerConst.TRX_ID;

@Component
@Aspect
public class RequestLoggingAspect {

    @Around(POINTCUT_WEBLAYER)
    public Object logInOut(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> clazz = joinPoint.getTarget().getClass();
        Logger logger = LoggerFactory.getLogger(clazz);

        Instant start = Instant.now();
        Object result = null;
        Throwable exception = null;
        try {
            result = joinPoint.proceed();
            if (result instanceof Mono<?> monoOut) {
                return logMonoResult(joinPoint, clazz, logger, start, monoOut);
            } else if (result instanceof Flux<?> fluxOut) {
                return logFluxResult(joinPoint, clazz, logger, start, fluxOut);
            } else {
                return result;
            }
        } catch (Throwable e) {
            exception = e;
            throw e;
        } finally {
            if (!(result instanceof Mono<?>) && !(result instanceof Flux<?>)) {
                doOutputLogging(joinPoint, clazz, logger, start, result, exception);
            }
        }
    }

    private <T, L> Mono<T> logMonoResult(ProceedingJoinPoint joinPoint, Class<L> clazz, Logger logger, Instant start, Mono<T> monoOut) {
        return Mono.deferContextual(contextView ->
                monoOut
                        .switchIfEmpty(Mono.<T>empty()
                                .doOnSuccess(logOnEmptyConsumer(contextView, () -> doOutputLogging(joinPoint, clazz, logger, start, "[empty]", null))))
                        .doOnEach(logOnNext(v -> doOutputLogging(joinPoint, clazz, logger, start, v, null)))
                        .doOnEach(logOnError(e -> doOutputLogging(joinPoint, clazz, logger, start, null, e)))
                        .doOnCancel(logOnEmptyRunnable(contextView, () -> doOutputLogging(joinPoint, clazz, logger, start, "[cancelled]", null)))
        );
    }

    private <T> Flux<T> logFluxResult(ProceedingJoinPoint joinPoint, Class<?> clazz, Logger logger, Instant start, Flux<T> fluxOut) {
        return Flux.deferContextual(contextView ->
                fluxOut
                        .switchIfEmpty(Flux.<T>empty()
                                .doOnComplete(logOnEmptyRunnable(contextView, () -> doOutputLogging(joinPoint, clazz, logger, start, "[empty]", null))))
                        .doOnEach(logOnNext(v -> doOutputLogging(joinPoint, clazz, logger, start, v, null)))
                        .doOnEach(logOnError(e -> doOutputLogging(joinPoint, clazz, logger, start, null, e)))
                        .doOnCancel(logOnEmptyRunnable(contextView, () -> doOutputLogging(joinPoint, clazz, logger, start, "[cancelled]", null)))
        );
    }

    private static <T> Consumer<Signal<T>> logOnNext(Consumer<T> logStatement) {
        return signal -> {
            if (!signal.isOnNext()) return;

            String trxIdVar = signal.getContextView().getOrDefault(TRX_ID, "");
            String pathUriVar = signal.getContextView().getOrDefault(PATH_URI, "");
            try (MDC.MDCCloseable trx = MDC.putCloseable(TRX_ID, trxIdVar);
                 MDC.MDCCloseable path = MDC.putCloseable(PATH_URI, pathUriVar)) {
                T t = signal.get();
                logStatement.accept(t);
            }
        };
    }

    public static <T> Consumer<Signal<T>> logOnError(Consumer<Throwable> errorLogStatement) {
        return signal -> {
            if (!signal.isOnError()) return;
            String trxIdVar = signal.getContextView().getOrDefault(TRX_ID, "");
            String pathUriVar = signal.getContextView().getOrDefault(PATH_URI, "");
            try (MDC.MDCCloseable trx = MDC.putCloseable(TRX_ID, trxIdVar);
                 MDC.MDCCloseable path = MDC.putCloseable(PATH_URI, pathUriVar)) {
                errorLogStatement.accept(signal.getThrowable());
            }
        };
    }

    private static <T> Consumer<T> logOnEmptyConsumer(final ContextView contextView, Runnable logStatement) {
        return signal -> {
            if (signal != null) return;
            String trxIdVar = contextView.getOrDefault(TRX_ID, "");
            String pathUriVar = contextView.getOrDefault(PATH_URI, "");
            try (MDC.MDCCloseable trx = MDC.putCloseable(TRX_ID, trxIdVar);
                 MDC.MDCCloseable path = MDC.putCloseable(PATH_URI, pathUriVar)) {
                logStatement.run();
            }
        };
    }

    private static Runnable logOnEmptyRunnable(final ContextView contextView, Runnable logStatement) {
        return () -> {
            String trxIdVar = contextView.getOrDefault(TRX_ID, "");
            String pathUriVar = contextView.getOrDefault(PATH_URI, "");
            try (MDC.MDCCloseable trx = MDC.putCloseable(TRX_ID, trxIdVar);
                 MDC.MDCCloseable path = MDC.putCloseable(PATH_URI, pathUriVar)) {
                logStatement.run();
            }
        };
    }

    private <T> void doOutputLogging(final ProceedingJoinPoint joinPoint, final Class<?> clazz, final Logger logger,
                                     final Instant start, final T result, final Throwable exception) {
        //log(...);
        //db.insert(...);
        if (exception != null)
            logger.info("Occur error", exception);

        if (result != null)
            logger.info("The result is [{}]", result);
    }

}
