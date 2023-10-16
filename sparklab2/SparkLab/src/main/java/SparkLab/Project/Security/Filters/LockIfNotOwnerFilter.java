package SparkLab.Project.Security.Filters;

import DatabaseInterface.MongoDatabaseAdapter;

import DatabaseInterface.Project;
import DatabaseInterface.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * Lock some request for unauthorized path */
public class LockIfNotOwnerFilter extends OncePerRequestFilter {

    MongoDatabaseAdapter mongo;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        //Blocca la richiesta per la pagina dei proprietari del progetto se non sei il proprietario del progetto per cui stai richiedendo la pagina
        if (request.getRequestURL().toString().contains("/ProjectPage2")) {

            mongo = MongoDatabaseAdapter.getIstance();
            System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
            User user = mongo.getUserByNicknameWithProjects(SecurityContextHolder.getContext().getAuthentication().getName());
            Project proj = mongo.getProject(request.getParameter("id"));

            if (!proj.getOwnerId().equals(user.getId())) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }


        }


              filterChain.doFilter(request,response);

    }
}

