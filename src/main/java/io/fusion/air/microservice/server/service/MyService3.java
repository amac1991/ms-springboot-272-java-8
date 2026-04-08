package io.fusion.air.microservice.server.service;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Component
public class MyService3 {

    @Autowired
    private EchoService echoService;

    @Autowired
    private EchoSessionService echoSessionService;

    @Autowired
    private EchoAppService echoAppService;

    public void printData() {
        System.out.println("MyService3:Request-Scope: " + Objects.toString(echoService.getEchoData()));
        System.out.println("MyService3:Session-Scope: " + Objects.toString(echoSessionService.getEchoData()));
        System.out.println("MyService3:Apps----Scope: " + Objects.toString(echoAppService.getEchoData()));
    }
}
