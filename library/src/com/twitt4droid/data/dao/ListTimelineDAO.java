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
 * List Data Access Object interface.
 *
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public interface ListTimelineDAO extends GenericDAO<Status, Long> {

    /**
     * Returns all the statuses from the given list.
     * 
     * @param listId the list id.
     * @return statuses.
     */
    List<Status> fetchListByListId(Long listId);

    /**
     * Saves all the given statuses in the given list.
     * 
     * @param statuses statuses.
     * @param listId the list id.
     */
    void save(List<Status> statuses, Long listId);

    /**
     * Deletes all statuses in the given list.
     * 
     * @param listId the list id.
     */
    void deleteAllByListId(Long listId);
}