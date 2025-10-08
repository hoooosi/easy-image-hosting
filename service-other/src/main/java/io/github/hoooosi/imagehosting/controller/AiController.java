package io.github.hoooosi.imagehosting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// TODO
@RestController
@RequestMapping("/ai")
@Slf4j
@AllArgsConstructor
@Tag(name = "AI I/F")
public class AiController {


    @GetMapping
    @Operation(summary = "DESCRIBE IMAGE")
    public void describe(Long idxId) {
    }

    @GetMapping
    @Operation(summary = "AI MAP EXPANSION")
    public void expansion(Long idxId) {

    }
}
