package com.n1nt3nd0.moneroexchangeapp.service;

import lombok.extern.slf4j.Slf4j;
import monero.daemon.MoneroDaemon;
import monero.daemon.MoneroDaemonRpc;
import monero.daemon.model.MoneroNetworkType;
import monero.daemon.model.MoneroTx;
import monero.wallet.MoneroWalletFull;
import monero.wallet.MoneroWalletRpc;
import monero.wallet.model.MoneroOutputWallet;
import monero.wallet.model.MoneroTxWallet;
import monero.wallet.model.MoneroWalletConfig;
import monero.wallet.model.MoneroWalletListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
@Slf4j
public class MoneroService {

    public ResponseEntity<?> createDaemonRpc(){
        try {
            MoneroDaemon daemon = new MoneroDaemonRpc("node.moneroworld.com:18089");
            long height = daemon.getHeight();                       // test
            List<MoneroTx> txsInPool = daemon.getTxPool(); // test
            log.info(daemon.getInfo().toString());
            return ResponseEntity.ok(daemon.getInfo().toString());
        }catch (Exception exception){
            log.info("error while create daemonRpc");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<?> createMoneroWalletFull(){
        // create wallet from mnemonic phrase using JNI bindings to monero-project
        MoneroDaemon daemon = new MoneroDaemonRpc("node.moneroworld.com:18089");
        MoneroWalletFull walletFull = null;
        try {
            walletFull = MoneroWalletFull.createWallet(new MoneroWalletConfig()
                    .setPath("sample_wallet_full")
                    .setPassword("supersecretpassword123")
                    .setNetworkType(MoneroNetworkType.MAINNET)
                    .setServerUri("node.moneroworld.com:18089")
                    .setServerUsername("superuser")
                    .setServerPassword("abctesting123")
                    .setSeed("hefty value scenic...")
                    .setRestoreHeight(573936l));
            System.out.println(walletFull.toString());
        } catch (Exception e) {
            throw new RuntimeException("error while create wallet: "+e.getMessage());
        }
        return ResponseEntity.ok(walletFull.toString());
    }
    public ResponseEntity<?> connectToWalletRpc(){
        // connect to wallet RPC and open wallet
        try {
            MoneroWalletRpc walletRpc = new MoneroWalletRpc("node.moneroworld.com:18089");
            walletRpc.openWallet("sample_wallet_rpc", "supersecretpassword123");
            String primaryAddress = walletRpc.getPrimaryAddress();  // 555zgduFhmKd2o8rPUz...
            log.info(primaryAddress);
            BigInteger balance = walletRpc.getBalance();            // 533648366742
            log.info(balance.toString());
            List<MoneroTxWallet> txs = walletRpc.getTxs();
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            throw new RuntimeException("error while connect to wallet rpc: "+e.getMessage());
        }
    }
    public ResponseEntity<?> getTransactionByHash(String hash){
        try {
            MoneroDaemon daemon = new MoneroDaemonRpc("node.moneroworld.com:18089");
            MoneroTx tx = daemon.getTx(hash);
            tx.getHeight();
            return ResponseEntity.ok(tx);
        } catch (Exception e) {
            throw new RuntimeException("error while getTransactionByHash: "+e.getMessage());
        }
    }
}
