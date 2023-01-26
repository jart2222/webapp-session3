package org.aguzman.apiservlet.webapp.headers.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.aguzman.apiservlet.webapp.headers.models.Producto;
import org.aguzman.apiservlet.webapp.headers.services.ServiceJdbcException;
import org.aguzman.apiservlet.webapp.headers.util.ConexionBaseDatos;
import org.aguzman.apiservlet.webapp.headers.util.ConexionBaseDatosDS;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebFilter("/*")
public class ConexionFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try(Connection conn= ConexionBaseDatosDS.getConnection()) {
            if (conn.getAutoCommit()){
                conn.setAutoCommit(false);
            }
            try {

                request.setAttribute("conn", conn);
                chain.doFilter(request,response);
                conn.commit();
            }catch (SQLException | ServiceJdbcException e){
                conn.rollback();
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
    }
}
