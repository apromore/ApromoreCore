/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Stores the process in apromore.
 * @author Cameron James
 */
@Entity
@Table(name = "membership")
@Configurable("membership")
@Cache(expiry = 180000, size = 100, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class Membership implements Serializable {

    private Integer id;
    private String password;
    private String salt;
    private String mobilePin;
    private String email;
    private String question;
    private String answer;
    private boolean isApproved;
    private boolean isLocked;
    private Date dateCreated;
    private int failedPasswordAttempts;
    private int failedAnswerAttempts;

    private User user;


    /**
     * Default Constructor.
     */
    public Membership() {
    }


    /**
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    /**
     * Set the id for the Object.
     * @param newId The role name to set.
     */
    public void setId(final Integer newId) {
        this.id = newId;
    }


    /**
     * Get the password for the Object.
     * @return Returns the password.
     */
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    /**
     * Set the password for the Object.
     * @param newPassword The password to set.
     */
    public void setPassword(final String newPassword) {
        this.password = newPassword;
    }

    /**
     * Get the password salt for the Object.
     * @return Returns the password salt.
     */
    @Column(name = "password_salt")
    public String getSalt() {
        return salt;
    }

    /**
     * Set the password salt for the Object.
     * @param newSalt The password salt to set.
     */
    public void setSalt(final String newSalt) {
        this.salt = newSalt;
    }

    /**
     * Get the mobile pin for the Object.
     * @return Returns the mobile pin.
     */
    @Column(name = "mobile_pin")
    public String getMobilePin() {
        return mobilePin;
    }

    /**
     * Set the mobile pin for the Object.
     * @param newMobilePin The mobile pin to set.
     */
    public void setMobilePin(final String newMobilePin) {
        this.mobilePin = newMobilePin;
    }

    /**
     * Get the email for the Object.
     * @return Returns the email.
     */
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    /**
     * Set the email for the Object.
     * @param newEmail The email to set.
     */
    public void setEmail(final String newEmail) {
        this.email = newEmail;
    }

    /**
     * Get the security question for the Object.
     * @return Returns the security question.
     */
    @Column(name = "password_question", nullable = true)
    public String getQuestion() {
        return question;
    }

    /**
     * Set the security question for the Object.
     * @param newQuestion The security question to set.
     */
    public void setQuestion(final String newQuestion) {
        this.question = newQuestion;
    }

    /**
     * Get the security answer for the Object.
     * @return Returns the security answer.
     */
    @Column(name = "password_answer", nullable = true)
    public String getAnswer() {
        return answer;
    }

    /**
     * Set the security answer for the Object.
     * @param newAnswer The security answer to set.
     */
    public void setAnswer(final String newAnswer) {
        this.answer = newAnswer;
    }

    /**
     * Get the approved flag for the Object.
     * @return Returns the approved flag.
     */
    @Column(name = "is_approved")
    public boolean getIsApproved() {
        return isApproved;
    }

    /**
     * Set the approved flag for the Object.
     * @param newIsApproved The approved flag to set.
     */
    public void setIsApproved(final boolean newIsApproved) {
        this.isApproved = newIsApproved;
    }

    /**
     * Get the locked flag for the Object.
     * @return Returns the locked flag.
     */
    @Column(name = "is_locked")
    public boolean getIsLocked() {
        return isLocked;
    }

    /**
     * Set the locked flag for the Object.
     * @param newIsLocked The locked flag to set.
     */
    public void setIsLocked(final boolean newIsLocked) {
        this.isLocked = newIsLocked;
    }

    /**
     * Get the date created for the Object.
     * @return Returns the date created.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "date_created")
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * Set the date created for the Object.
     * @param newDateCreated The date created to set.
     */
    public void setDateCreated(final Date newDateCreated) {
        this.dateCreated = newDateCreated;
    }

    /**
     * Get the failed password attempts for the Object.
     * @return Returns the failed password attempts.
     */
    @Column(name = "failed_password_attempts")
    public int getFailedPasswordAttempts() {
        return failedPasswordAttempts;
    }

    /**
     * Set the failed password attempts for the Object.
     * @param newFailedPasswordAttempts The failed password attempts to set.
     */
    public void setFailedPasswordAttempts(final int newFailedPasswordAttempts) {
        this.failedPasswordAttempts = newFailedPasswordAttempts;
    }

    /**
     * Get the failed answer attempts for the Object.
     * @return Returns the failed answer attempts.
     */
    @Column(name = "failed_answer_attempts", unique = false, nullable = false)
    public int getFailedAnswerAttempts() {
        return failedAnswerAttempts;
    }

    /**
     * Set the failed answer attempts for the Object.
     * @param newFailedAnswerAttempts The failed answer attempts to set.
     */
    public void setFailedAnswerAttempts(final int newFailedAnswerAttempts) {
        this.failedAnswerAttempts = newFailedAnswerAttempts;
    }

    /**
     * Get the user for the Object.
     * @return Returns the user.
     */
    @OneToOne
    @JoinColumn(name = "UserId", nullable = false)
    public User getUser() {
        return this.user;
    }

    /**
     * Set the user for the Object.
     * @param newUser The user to set.
     */
    public void setUser(final User newUser) {
        this.user = newUser;
    }

}
