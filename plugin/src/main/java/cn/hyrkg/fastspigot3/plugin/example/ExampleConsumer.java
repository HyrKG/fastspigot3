package cn.hyrkg.fastspigot3.plugin.example;

import cn.hyrkg.fastspigot3.annotation.Inject;

public class ExampleConsumer {

    @Inject
    private ExampleService exampleService;

    public String hello() {
        return exampleService == null ? "null" : exampleService.message();
    }
}


