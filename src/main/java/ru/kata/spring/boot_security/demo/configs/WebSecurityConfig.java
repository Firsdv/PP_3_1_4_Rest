package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SuccessUserHandler successUserHandler;//обработака аутентификации и куда перенаправить пользователя после успешного входа в систему.
    private final UserServiceImpl userServiceImpl;//Этот сервис используется для аутентификации пользователей.

    public WebSecurityConfig(SuccessUserHandler successUserHandler,
                             UserServiceImpl userServiceImpl) {//контструктор внедрения Dependency Injection
        this.successUserHandler = successUserHandler;
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {//метод для настройки авторизации
        http
                .authorizeRequests() //начинает настройку авторизации запросов
                .antMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN")//все URL-адреса, начинающиеся с /admin/, требуют, чтобы пользователь имел роль ROLE_ADMIN
                .antMatchers("/user/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")//использует Ant-style path patterns для сопоставления URL
                .antMatchers("/", "/login").permitAll()
                .anyRequest().authenticated()//Указывает, что все остальные URL-адреса (кроме перечисленных выше) требуют аутентификации. Пользователь должен быть залогинен для доступа к ним.
                .and()//Используется для соединения различных конфигураций безопасности.
                .formLogin()//Включает поддержку формы логина.
                .successHandler(successUserHandler)//Указывает, что после успешной аутентификации будет вызван successUserHandler для обработки дальнейших действий (например, перенаправления пользователя).
                .permitAll()//Указывает, что форма логина доступна для всех.
                .and()
                .logout()//Включает поддержку logout.
                .permitAll();// Указывает, что logout доступен для всех. Обычно при выходе из системы пользователя перенаправляет на страницу входа или главную страницу.
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {//метод Настройка аутентификации
        auth.userDetailsService(userServiceImpl).passwordEncoder(passwordEncoder());
    }//Переопределяет метод configure класса WebSecurityConfigurerAdapter для настройки аутентификации.
    // AuthenticationManagerBuilder используется для создания AuthenticationManager, который отвечает за аутентификацию пользователей
    // для то для получения информации о пользователях (имя пользователя, пароль, роли) будет использоваться userServiceImpl.
    //Указывает, какой кодировщик паролей будет использоваться. В данном случае используется BCryptPasswordEncoder.
    // Важно использовать кодировщик паролей, чтобы хранить пароли в базе данных в зашифрованном виде.

    @Bean
    public PasswordEncoder passwordEncoder() {//Определение бинов:
        return new BCryptPasswordEncoder();
    }//Определяет бин passwordEncoder типа PasswordEncoder. Spring создаст экземпляр BCryptPasswordEncoder и
    // сделает его доступным для внедрения в другие компоненты. BCryptPasswordEncoder - это надежный алгоритм хеширования паролей.

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoauthenticationProvider = new DaoAuthenticationProvider();
        daoauthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoauthenticationProvider.setUserDetailsService(userServiceImpl);
        return daoauthenticationProvider;
    }
}//Определяет бин daoAuthenticationProvider типа DaoAuthenticationProvider. DaoAuthenticationProvider является реализацией AuthenticationProvider,
// который аутентифицирует пользователей на основе данных, полученных из UserDetailsService. Он использует passwordEncoder для проверки паролей.
// Этот бин настраивается с помошью passwordEncoder и userDetailsService.
// Это более явный способ настройки аутентификации, чем просто использование auth.userDetailsService(userServiceImpl).passwordEncoder(passwordEncoder())