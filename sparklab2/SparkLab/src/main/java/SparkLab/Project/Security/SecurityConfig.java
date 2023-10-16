package SparkLab.Project.Security;

import SparkLab.Project.Security.Filters.LockIfNotOwnerFilter;
import SparkLab.Project.Security.Filters.LockIfNotPartecipatingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {



        http.authorizeHttpRequests().requestMatchers("basicPage/login.html").permitAll().and().addFilterAfter(new LockIfNotOwnerFilter(),UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new LockIfNotPartecipatingFilter() ,LockIfNotOwnerFilter.class);




        http.headers().frameOptions().sameOrigin(); // se questa riga viene rimossa questo maledetto di security impedisce la visualizzazione dei contenuti inviati negli iframe

        http.authorizeHttpRequests().requestMatchers("basicPage/ProjectRes/**").permitAll();
  
        http.authorizeHttpRequests().requestMatchers("/ResourceAPI/**").permitAll();
        http.authorizeHttpRequests().requestMatchers("/CommentAPI/**").permitAll();
        http.authorizeHttpRequests().requestMatchers("basicPage/Sperimentazione.html").hasRole("CUSTOMER")
                .and().logout().logoutUrl("/logout").invalidateHttpSession(true).and()
                .formLogin()
                .loginPage("/basicPage/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/basicPage/Sperimentazione.html", false) // Pagina di destinazione dopo il login avvenuto con successo
                .permitAll().and().csrf().disable();

        http.authorizeHttpRequests().requestMatchers("basicPage/ProjectPage2.html").hasRole("CUSTOMER")
                .and().logout().logoutUrl("/logout").invalidateHttpSession(true).and()
                .formLogin()
                .loginPage("/basicPage/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/basicPage/ProjectPage2.html", false) // Pagina di destinazione dopo il login avvenuto con successo
                .permitAll().and().csrf().disable();

        http.authorizeHttpRequests().requestMatchers("basicPage/ProjectPage3.html").hasRole("CUSTOMER")
                .and()
                .formLogin()
                .loginPage("/basicPage/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/basicPage/ProjectPage3.html", false) // Pagina di destinazione dopo il login avvenuto con successo
                .permitAll().and().csrf().disable();

    	http.authorizeHttpRequests().requestMatchers("basicPage/ProjectPage.html").hasRole("CUSTOMER")
        .and()
        .formLogin()
        .loginPage("/basicPage/login.html")
        .loginProcessingUrl("/login")
        .defaultSuccessUrl("/basicPage/ProjectPage.html", false) // Pagina di destinazione dopo il login avvenuto con successo
        .permitAll().and().csrf().disable();

        http.authorizeHttpRequests().requestMatchers("basicPage/ProjectPage4.html").hasRole("CUSTOMER")
                .and()
                .formLogin()
                .loginPage("/basicPage/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/basicPage/ProjectPage4.html", false) // Pagina di destinazione dopo il login avvenuto con successo
                .permitAll().and().csrf().disable();


        http.authorizeHttpRequests().requestMatchers("basicPage/ShowR.html").hasRole("CUSTOMER")
                .and()
                .formLogin()
                .loginPage("/basicPage/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/basicPage/ProjectPage.html", false) // Pagina di destinazione dopo il login avvenuto con successo
                .permitAll().and().csrf().disable();



        http.authorizeHttpRequests().requestMatchers("/basicPage/pages_resources/**").hasRole("CUSTOMER")
                .and()
                .formLogin()
                .loginPage("/basicPage/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/basicPage/HomePage.html", false).and().logout().logoutUrl("/logout")// Pagina di destinazione dopo il login avvenuto con successo
                .permitAll().and().csrf().disable();

        http.authorizeHttpRequests().requestMatchers("/ProjectAPI/project").permitAll().and().anonymous();

        http.authorizeHttpRequests().requestMatchers("/login.html").permitAll();
        http.authorizeHttpRequests().requestMatchers("/UserAPI/**").permitAll();


        http.authorizeHttpRequests().requestMatchers("ProjectAPI/**").permitAll();
        http.authorizeHttpRequests().requestMatchers("SubPlanAPI/**").permitAll();
        http.authorizeHttpRequests().requestMatchers("CommentAPI/**").permitAll();
        http.authorizeHttpRequests().requestMatchers("/basicPage/Registration.html").permitAll();

        http.authorizeHttpRequests().requestMatchers("/basicPage/Comments.html").permitAll();
        //security homepage
        http.authorizeHttpRequests().requestMatchers("/basicPage/HomePage.html").hasRole("CUSTOMER")
                .and()
                .formLogin()
                .loginPage("/basicPage/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/basicPage/HomePage.html", false) // Pagina di destinazione dopo il login avvenuto con successo
                .permitAll().and().csrf().disable();

        //security UserPage
        http.authorizeHttpRequests().requestMatchers("/basicPage/UserPage.html").hasRole("CUSTOMER")
                .and()
                .formLogin()
                .loginPage("/basicPage/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/basicPage/UserPage.html", false)// Pagina di destinazione dopo il login avvenuto con successo
                .permitAll().and().csrf().disable();

        http.authorizeHttpRequests().requestMatchers("/basicPage/ProjectCreation.html").hasRole("CUSTOMER")
                .and()
                .formLogin()
                .loginPage("/basicPage/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/basicPage/HomePage.html", false) // Pagina di destinazione dopo il login avvenuto con successo
                .permitAll().and().csrf().disable();

        http.authorizeHttpRequests().requestMatchers("/basicPage/UserPage2.html").hasRole("CUSTOMER")
                .and()
                .formLogin()
                .loginPage("/basicPage/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/basicPage/HomePage.html", false)// Pagina di destinazione dopo il login avvenuto con successo
                .permitAll().and().csrf().disable();


       http.sessionManagement().maximumSessions(10);
        return http.build();



    }
}