package io.crowdcode.jopt.joptbay.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ProfilerController {

    @PutMapping("/profiling/start")
    public ResponseEntity<Void> startProfiling() {
        log.info("Start profiling method called");
        return ResponseEntity.ok().build();
    }


    @PutMapping("/profiling/stop")
    public ResponseEntity<Void> stopProfiling() {
        log.info("Stop profiling method called.");
        return ResponseEntity.ok().build();
    }

    @PutMapping("/shutdown")
    public ResponseEntity<Void> shutdown() {
        new Thread(() -> {
            log.info("Shutting down system");
            System.exit(0);
        }).start();
        return ResponseEntity.accepted().build();
    }

}
