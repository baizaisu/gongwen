package com.example.gongwen.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    //映射到hello
    @RequestMapping("/hello")
    public String helloWorld(){
        //返回字符串
        return "hello,world";
    }

    //传参数
    @RequestMapping("/helloFromName")
    public String helloFromName(@RequestParam(value = "name")String name){
        //返回字符串
        return "hello," + name;
    }

    public static void main(String[] args) {
        HelloController helloController = new HelloController();
        helloController.helloWorld();
        helloController.helloFromName("baifeng");
    }
}

