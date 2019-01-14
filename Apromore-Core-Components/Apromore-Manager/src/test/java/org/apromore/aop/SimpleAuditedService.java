/*
 * Copyright (c) 2010 Xebia and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
