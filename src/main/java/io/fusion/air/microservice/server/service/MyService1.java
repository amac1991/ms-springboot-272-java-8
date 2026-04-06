package io.fusion.air.microservice.server.service;

import java.util.Objects;
import io.fusion.air.microservice.adapters.filters.HeaderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Component
public class MyService1 {

    @Autowired
    private EchoService echoService;

    @Autowired
    private EchoSessionService echoSessionService;

    @Autowired
    private EchoAppService echoAppService;

    public void printData() {
        System.out.println("MyService1:Request-Scope: " + Objects.toString(echoService.getEchoData()));
        System.out.println("MyService1:Session-Scope: " + Objects.toString(echoSessionService.getEchoData()));
        System.out.println("MyService1:Apps----Scope: " + Objects.toString(echoAppService.getEchoData()));
    }
}
