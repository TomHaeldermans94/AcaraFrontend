package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CategoryDto;
import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.controller.dto.EventDtoList;

import java.util.List;
import java.util.Map;

public interface EventService {
    /**
     * Gets the event matching the given id
     * @param id the id to find the event with
     * @return the event matching the given id
     */
    EventDto getEvent(Long id);

    /**
     * Deletes an event
     * @param id the id of the event to delete
     */
    void delete(Long id);

    /**
     * Gets all the events. If there are non-default parameters provided, the matching results are returned.
     *
     * @param params the filtering params
     * @param page the page of the result
     * @param size how many results there are on one page
     * @param sort on which field to sort and in which direction, multiple sorts are supported
     * @return the eventlist matching all provided params
     */
    EventDtoList findAllEvents(Map<String, String> params, int page, int size, String sort);

    /**
     * Gets all categories
     * @return a list of categories
     */
    List<CategoryDto> getCategories();

    /**
     * Adds an event to the service
     * @param eventDto the event to be created
     */
    void addEvent(EventDto eventDto);

    /**
     * Edits an event
     * @param id the id of the event to edit
     * @param eventDto the new body of the event
     */
    void editEvent(Long id, EventDto eventDto);

    /**
     * Gets all events the given user is subscribed to
     *
     * @param id the id of the user
     * @param page the desired page of the results to get
     * @param size the amount of results a single page can contain
     * @return an EventDtoList containing all results
     */
    EventDtoList getEventsFromUser(Long id, int page, int size);

    /**
     * Gets all events the given user is currently liking
     *
     * @param id the id of the user
     * @param page the desired page of the results to get
     * @param size the amount of results a single page can contain
     * @return an EventDtoList containing all results
     */
    EventDtoList getEventsThatUserLiked(Long id,  int page, int size);
}
