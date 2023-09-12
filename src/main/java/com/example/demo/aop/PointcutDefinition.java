package com.example.demo.aop;

import org.aspectj.lang.annotation.Pointcut;

public class PointcutDefinition {

    @Pointcut("@within(org.springframework.stereotype.Controller) || " +
            "@within(org.springframework.web.bind.annotation.RestController)")
    public void webLayer() {
    }
}
