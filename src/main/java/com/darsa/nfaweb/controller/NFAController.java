package com.darsa.nfaweb.controller;

import com.darsa.nfaweb.services.nfa.NFAService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nfa")
@RequiredArgsConstructor
public class NFAController {

    private final NFAService nfaService;

    @GetMapping("/compile")
    public String compile(@RequestParam String regex) {
        return nfaService.compile(regex).toString();
    }
}
