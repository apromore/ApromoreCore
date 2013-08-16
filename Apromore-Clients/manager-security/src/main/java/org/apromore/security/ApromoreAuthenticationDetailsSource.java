package org.apromore.security;

public interface ApromoreAuthenticationDetailsSource <C, B, T> {
    T buildDetails(C c, B b);
}
