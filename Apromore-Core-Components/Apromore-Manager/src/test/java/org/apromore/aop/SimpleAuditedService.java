/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2013 Queensland University of Technology.
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.aop;

/**
 * @author David Galichet
 */
public class SimpleAuditedService {

    @Audited(message = "save(#{args[0].name}, #{args[0].email}): #{returned?.id}")
    public Customer save(Customer customer) {
        if (customer.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3}$")) {
            customer.setId(324325L);
            return customer;
        } else {
            throw new IllegalArgumentException("invalid email");
        }
    }

    public static class Customer {
        private Long id;
        private String name;
        private String email;

        public Long getId() {
            return id;
        }

        private void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String toString() {
            return String.format("[id=%s,name=%s,email=%s]", id, name, email);
        }
    }
}
