package io.fusion.air.microservice.server.service;

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
        System.out.println("MyService1:Request-Scope: " + String.valueOf(echoService.getEchoData()));
        System.out.println("MyService1:Session-Scope: " + String.valueOf(echoSessionService.getEchoData()));
        System.out.println("MyService1:Apps----Scope: " + String.valueOf(echoAppService.getEchoData()));
    }
}
