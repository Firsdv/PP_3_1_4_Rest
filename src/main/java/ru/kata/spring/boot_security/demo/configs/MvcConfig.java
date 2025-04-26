package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/user").setViewName("user");
        //регистрирует и находит /user представление user.html
        //возвращает имя представления user Spring MVC использует настроенный ViewResolver (решатель представлений)
        //Когда кто-то заходит на URL /user, покажи ему представление, которое называется user (например,
        // файл user.html или user.jsp).” Этот подход полезен для простых случаев, когда вам просто нужно отобразить
        // статический контент без необходимости сложной логики в контроллере
    }
}