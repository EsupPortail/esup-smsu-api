package org.esupportail.smsuapi.web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.esupportail.smsuapi.utils.HibernateUtils;
import jakarta.inject.Inject;

public class TransactionManagerFilter implements Filter {

    @Inject private SessionFactory sessionFactory;

    public void destroy() {}
    public void init(FilterConfig config) {}

    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        boolean participate = HibernateUtils.openSession(sessionFactory);
        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
        try {
            filterChain.doFilter(request, response);
        } finally {
            try {
                transaction.commit();
            }
            catch (Exception e) {
                transaction.rollback();
            }
            HibernateUtils.closeSession(sessionFactory, participate);
        }
    }
}