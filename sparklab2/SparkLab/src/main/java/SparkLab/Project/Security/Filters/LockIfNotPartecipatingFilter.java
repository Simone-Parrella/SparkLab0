package SparkLab.Project.Security.Filters;

import DatabaseInterface.MongoDatabaseAdapter;

import DatabaseInterface.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class LockIfNotPartecipatingFilter extends OncePerRequestFilter {

    MongoDatabaseAdapter mongo;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        //Blocca la richiesta per la pagina dei partecipanti al progetto se non sei un partecipante al progetto per cui stai richiedendo la pagina
        if(request.getRequestURL().toString().contains("/ProjectPage.html")){
            mongo= MongoDatabaseAdapter.getIstance();
            System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
            User res = mongo.getUserByNicknameWithProjects(SecurityContextHolder.getContext().getAuthentication().getName());
            String proj = request.getParameter("id");

            if(!res.getProjectId().contains(proj)){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

        }

            filterChain.doFilter(request, response);

    }

}
