package com.springboot.retrospective;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/retrospectives")
public class RetrospectiveController {

	private final Logger LOGGER = LoggerFactory.getLogger(RetrospectiveController.class);

	private List<Retrospective> retrospectives = new ArrayList<>();

	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createRetrospective(@RequestBody Retrospective retrospective) {
		LOGGER.info("Creating the retrospective...");

		try {
			if (retrospective.getDate() == null || retrospective.getParticipants().isEmpty()) {
				LOGGER.error("Invalid request: Date and participants are required.");
				return ResponseEntity.badRequest().body("Date and participants are required.");
			}

			retrospectives.add(retrospective);
			LOGGER.info("Retrospective created: {}", retrospective.getName());
			return ResponseEntity.status(HttpStatus.CREATED).body("Retrospective created.");
		} catch (Exception e) {
			LOGGER.error("Error creating retrospective: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
		}
	}

	@PostMapping(value = "/{name}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addFeedback(@PathVariable String name,
			@RequestBody Map<String, Map<String, Object>> feedbackMap) {
		LOGGER.info("Adding feedback items to retrospective: {}", name);

		try {
			Optional<Retrospective> optionalRetrospective = retrospectives.stream()
					.filter(retrospective -> retrospective.getName().equals(name)).findFirst();

			if (optionalRetrospective.isPresent()) {
				Retrospective retrospective = optionalRetrospective.get();

				Map<String, Object> itemsMap = feedbackMap.get("Feedback");

				if (itemsMap != null) {
					for (Map.Entry<String, Object> entry : itemsMap.entrySet()) {
						Map<String, Object> itemMap = (Map<String, Object>) entry.getValue();
						Item item = new Item();
						item.setName((String) itemMap.get("Name"));
						item.setBody((String) itemMap.get("Body"));
						item.setFeedbackType((String) itemMap.get("FeedbackType"));

						Feedback feedbackItem = new Feedback();
						feedbackItem.setItem(Collections.singletonList(item));

						retrospective.getFeedBack().add(feedbackItem);
					}

					LOGGER.info("Received {} feedback items", itemsMap.size());
					LOGGER.info("Feedback items added to retrospective: {}", name);
					return ResponseEntity.status(HttpStatus.CREATED).body("Feedback items added.");
				} else {
					LOGGER.error("Invalid JSON structure: 'Feedback' object not found.");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON structure.");
				}
			} else {
				LOGGER.error("Retrospective not found: {}", name);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Retrospective not found.");
			}
		} catch (Exception e) {
			LOGGER.error("Error adding feedback items: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
		}
	}

	@PutMapping(value = "/{name}/feedback-items/{itemName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateFeedbackItemDetails(@PathVariable String name, @PathVariable String itemName,
			@RequestBody Item updatedFeedbackItem) {
		LOGGER.info("Updating feedback item details: Retrospective Name: {}, Item Name: {}", name, itemName);

		try {
			Optional<Retrospective> optionalRetrospective = retrospectives.stream()
					.filter(retrospective -> retrospective.getName().equals(name)).findFirst();

			if (optionalRetrospective.isPresent()) {
				Retrospective retrospective = optionalRetrospective.get();

				List<Feedback> feedbackList = retrospective.getFeedBack();
				for (Feedback feedback : feedbackList) {
					List<Item> items = feedback.getItem();
					for (Item item : items) {
						if (item.getName().equals(itemName)) {
							
							item.setBody(updatedFeedbackItem.getBody());
							item.setFeedbackType(updatedFeedbackItem.getFeedbackType());

							LOGGER.info("Feedback item updated: {}", itemName);
							return ResponseEntity.status(HttpStatus.OK).body("Feedback item updated.");
						}
					}
				}

				LOGGER.error("Feedback item not found: {}", itemName);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback item not found.");
			} else {
				LOGGER.error("Retrospective not found: {}", name);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Retrospective not found.");
			}
		} catch (Exception e) {
			LOGGER.error("Error updating feedback item details: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
		}
	}

	@GetMapping("/retrospectivesWithPagination")
	public ResponseEntity<List<Retrospective>> getAllRetrospectivesWithPagination(
			@RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int pageSize) {
		LOGGER.info("Returning retrospectives with pagination. Current Page: {}, Page Size: {}", currentPage, pageSize);

		try {
			int totalRetrospectives = retrospectives.size();
			int startIndex = currentPage * pageSize;
			int endIndex = Math.min(startIndex + pageSize, totalRetrospectives);

			if (startIndex >= totalRetrospectives) {
				LOGGER.info("No retrospectives to return.");
				return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
			}

			List<Retrospective> pagedRetrospectives = retrospectives.subList(startIndex, endIndex);

			LOGGER.info("Returned {} retrospectives.", pagedRetrospectives.size());

			return ResponseEntity.status(HttpStatus.OK).body(pagedRetrospectives);
		} catch (Exception e) {
			LOGGER.error("Error returning retrospectives: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping(value = "/searchRetrospectivesByDate", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<List<Retrospective>> searchRetrospectivesByDate(
			@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate searchDate,
			@RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int pageSize,
			@RequestHeader(name = "Accept", defaultValue = "application/json") String acceptHeader) {
		LOGGER.info("Searching retrospectives by date: Date: {}, Current Page: {}, Page Size: {}", searchDate,
				currentPage, pageSize);

		try {
			List<Retrospective> filteredRetrospectives = retrospectives.stream()
					.filter(retrospective -> retrospective.getDate().isEqual(searchDate)).collect(Collectors.toList());

			int startIndex = currentPage * pageSize;
			int endIndex = Math.min(startIndex + pageSize, filteredRetrospectives.size());

			if (startIndex >= filteredRetrospectives.size()) {
				LOGGER.info("No retrospectives found for the given date.");
				return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
			}

			List<Retrospective> pagedRetrospectives = filteredRetrospectives.subList(startIndex, endIndex);

			MediaType responseType = MediaType.APPLICATION_JSON;
			if (acceptHeader.contains(MediaType.APPLICATION_XML_VALUE)) {
				responseType = MediaType.APPLICATION_XML;
			}

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(responseType);

			LOGGER.info("Returned {} retrospectives for the given date.", pagedRetrospectives.size());

			return new ResponseEntity<>(pagedRetrospectives, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Error searching retrospectives by date: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
}
