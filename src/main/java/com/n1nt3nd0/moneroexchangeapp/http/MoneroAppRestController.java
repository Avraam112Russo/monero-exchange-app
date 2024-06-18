package com.n1nt3nd0.moneroexchangeapp.http;

import com.n1nt3nd0.moneroexchangeapp.service.MoneroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/monero")
@RequiredArgsConstructor
public class MoneroAppRestController {
    private final MoneroService moneroService;

    @GetMapping
    public ResponseEntity<?> createDaemonRpc(){
        return moneroService.createDaemonRpc();
    }
    @GetMapping("/wallet")
    public ResponseEntity<?> createMoneroWalletFull(){
        return moneroService.createMoneroWalletFull();
    }
    @GetMapping("/tx")
    public ResponseEntity<?> getTransactionByHash(@RequestParam String hash){
        return moneroService.getTransactionByHash(hash);
    }
}
