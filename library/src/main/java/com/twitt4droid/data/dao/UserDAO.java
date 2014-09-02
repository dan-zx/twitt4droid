/*
 * Copyright 2014 Daniel Pedraza-Arcega
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitt4droid.data.dao;

import twitter4j.User;

/**
 * User Data Access Object interface.
 *
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public interface UserDAO extends GenericDAO<User, Long> {

    /**
     * Return a user by the given id.
     * 
     * @param id a user id.
     * @return a user.
     */
    User fetchById(Long id);

    /**
     * Return a user by the given username.
     * 
     * @param screenName a username.
     * @return a user.
     */
    User fetchByScreenName(String screenName);

    /**
     * Saves the given user.
     * 
     * @param user a user.
     */
    void save(User user);

    /**
     * Deletes the given user.
     * 
     * @param user a user.
     */
    void delete(User user);
}