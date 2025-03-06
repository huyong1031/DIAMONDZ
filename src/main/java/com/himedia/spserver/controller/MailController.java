package com.himedia.spserver.controller;

import com.himedia.spserver.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class MailController {

    private final MailService ms;
    private int number;


    @PostMapping("/sendMail")
    public HashMap<String, Object> sendMail(@RequestParam("sellerEmail") String sellerEmail ) {
        HashMap<String, Object> result = new HashMap<>();
        number = ms.sendMail( sellerEmail );
        result.put("msg", "ok");
        return result;
    }

    @PostMapping("/codecheck")
    public HashMap<String, Object> codecheck(@RequestParam("sellercode") String sellercode ) {
        HashMap<String, Object> result = new HashMap<>();
        String num = String.valueOf(number);
        if( num.equals(sellercode) )
            result.put("msg", "ok");
        else
            result.put("msg", "not_ok");
        return result;
    }

}
