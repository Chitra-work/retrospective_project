package com.springboot.retrospective;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class RetrospectiveControllerTest {

    @InjectMocks
    private RetrospectiveController retrospectiveController;

    @Mock
    private List<Retrospective> retrospectives;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateRetrospective() {
        Retrospective retrospective = createSampleRetrospective();

        when(retrospectives.add(any(Retrospective.class))).thenReturn(true);

        ResponseEntity<String> response = retrospectiveController.createRetrospective(retrospective);

        assertCreatedResponse(response);
    }

    @Test
    public void testAddFeedback() {
        String retrospectiveName = "Sample Retrospective";
        Map<String, Map<String, Object>> feedbackMap = createSampleFeedbackMap();

        Retrospective retrospective = createSampleRetrospective();
        when(retrospectives.stream()).thenReturn(Stream.of(retrospective));

        ResponseEntity<String> response = retrospectiveController.addFeedback(retrospectiveName, feedbackMap);

        assertAddFeedbackResponse(response);
    }

    @Test
    public void testUpdateFeedbackItemDetails() {
        String retrospectiveName = "Sample Retrospective";
        String itemName = "Sample Item";
        Item updatedFeedbackItem = createUpdatedSampleItem();

        Retrospective retrospective = createSampleRetrospectiveWithFeedback(itemName);
        when(retrospectives.stream()).thenReturn(Stream.of(retrospective));

        ResponseEntity<String> response = retrospectiveController.updateFeedbackItemDetails(retrospectiveName, itemName, updatedFeedbackItem);

        assertOkResponse(response);

        assertFeedbackItemUpdated(retrospective, itemName, updatedFeedbackItem);
    }

    @Test
    public void testGetAllRetrospectivesWithPagination() {
        when(retrospectives.size()).thenReturn(5);
        when(retrospectives.subList(anyInt(), anyInt())).thenReturn(Collections.singletonList(new Retrospective()));

        ResponseEntity<List<Retrospective>> response = retrospectiveController.getAllRetrospectivesWithPagination(0, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testSearchRetrospectivesByDate() {
        LocalDate searchDate = LocalDate.now();
        String acceptHeader = "application/json";

        Retrospective retrospective = createSampleRetrospectiveWithDate(searchDate);
        when(retrospectives.stream()).thenReturn(Stream.of(retrospective));

        ResponseEntity<List<Retrospective>> response = retrospectiveController.searchRetrospectivesByDate(searchDate, 0, 5, acceptHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    private Retrospective createSampleRetrospective() {
        Retrospective retrospective = new Retrospective();
        retrospective.setName("Sample Retrospective");
        retrospective.setDate(LocalDate.now());
        retrospective.setParticipants(Collections.singletonList("User1"));
        return retrospective;
    }

    private Map<String, Map<String, Object>> createSampleFeedbackMap() {
        Map<String, Map<String, Object>> feedbackMap = new HashMap<>();

        Map<String, Object> feedbackData = new HashMap<>();
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("Name", "Sample Item");
        itemMap.put("Body", "Sample Body");
        itemMap.put("FeedbackType", "Sample Type");
        feedbackData.put("Item1", itemMap);
        feedbackMap.put("Feedback", feedbackData);

        return feedbackMap;
    }

    private Item createUpdatedSampleItem() {
        Item updatedFeedbackItem = new Item();
        updatedFeedbackItem.setBody("Updated Body");
        updatedFeedbackItem.setFeedbackType("Updated Type");
        return updatedFeedbackItem;
    }

    private Retrospective createSampleRetrospectiveWithFeedback(String itemName) {
        Retrospective retrospective = new Retrospective();
        retrospective.setName("Sample Retrospective");
        Feedback feedback = new Feedback();
        Item item = new Item();
        item.setName(itemName);
        feedback.setItem(Collections.singletonList(item));
        retrospective.getFeedBack().add(feedback);
        return retrospective;
    }

    private Retrospective createSampleRetrospectiveWithDate(LocalDate date) {
        Retrospective retrospective = new Retrospective();
        retrospective.setDate(date);
        return retrospective;
    }

    private void assertCreatedResponse(ResponseEntity<String> response) {
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Retrospective created.", response.getBody());
    }
    
    private void assertAddFeedbackResponse(ResponseEntity<String> response) {
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Feedback items added.", response.getBody());
    }


    private void assertOkResponse(ResponseEntity<String> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void assertFeedbackItemUpdated(Retrospective retrospective, String itemName, Item updatedItem) {
        assertEquals(updatedItem.getBody(), retrospective.getFeedBack().get(0).getItem().get(0).getBody());
        assertEquals(updatedItem.getFeedbackType(), retrospective.getFeedBack().get(0).getItem().get(0).getFeedbackType());
    }
}
