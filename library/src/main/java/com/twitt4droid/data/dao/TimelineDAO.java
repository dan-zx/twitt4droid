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

import java.util.List;

import twitter4j.Status;

/**
 * Timeline Data Access Object interface.
 *
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public interface TimelineDAO extends GenericDAO<Status, Long> { 

    /** 
     * Returns all statuses.
     * 
     * @return statuses. 
     */
    List<Status> fetchList();

    /**
     * Saves all the given statuses.
     * 
     * @param statuses statuses.
     */
    void save(List<Status> statuses);

    /** Deletes all statuses. */
    void deleteAll();
}