# retrospective_project

**Endpoints:**

Create New Retrospective : http://localhost:8080/retrospectives/create

Add Feedback Items to Retrospective : http://localhost:8080/retrospectives/{name}

Update Feedback Items : http://localhost:8080/retrospectives/{name}/feedback-items/{feedback-name}

Search Retrospective :

        Retrospective with Pagination : http://localhost:8080/retrospectives/retrospectivesWithPagination?currentPage=0&pageSize=10
        Retrospective by Date : http://localhost:8080/retrospectives/searchRetrospectivesByDate?date=2023-09-21
