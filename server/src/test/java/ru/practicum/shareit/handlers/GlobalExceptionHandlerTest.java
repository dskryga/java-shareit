package ru.practicum.shareit.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.NotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleNotFoundException_shouldReturnNotFoundResponse() {
        String errorMessage = "Item not found";
        NotFoundException exception = new NotFoundException(errorMessage);

        ErrorResponse response = globalExceptionHandler.handleNotFoundException(exception);

        assertNotNull(response);
        assertEquals(errorMessage, response.getDescription());
        assertEquals(404, response.getErrorCode());
    }

    @Test
    void handleAccessDeniedException_shouldReturnForbiddenResponse() {
        String errorMessage = "Access denied";
        AccessDeniedException exception = new AccessDeniedException(errorMessage);

        ErrorResponse response = globalExceptionHandler.handleAccessDeniedException(exception);

        assertNotNull(response);
        assertEquals(errorMessage, response.getDescription());
        assertEquals(403, response.getErrorCode());
    }

    @Test
    void handleNotFoundException_shouldReturnCorrectHttpStatus() throws Exception {
        NotFoundException exception = new NotFoundException("Not found");

        ResponseStatus responseStatus = GlobalExceptionHandler.class
                .getMethod("handleNotFoundException", NotFoundException.class)
                .getAnnotation(ResponseStatus.class);

        assertNotNull(responseStatus);
        assertEquals(HttpStatus.NOT_FOUND, responseStatus.value());
    }

    @Test
    void handleAccessDeniedException_shouldReturnCorrectHttpStatus() throws Exception {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        ResponseStatus responseStatus = GlobalExceptionHandler.class
                .getMethod("handleAccessDeniedException", AccessDeniedException.class)
                .getAnnotation(ResponseStatus.class);

        assertNotNull(responseStatus);
        assertEquals(HttpStatus.FORBIDDEN, responseStatus.value());
    }

    // Тесты для ErrorResponse
    @Test
    void errorResponse_shouldHaveCorrectBuilder() {
        ErrorResponse response = ErrorResponse.builder()
                .description("Test error")
                .errorCode(500)
                .build();

        assertNotNull(response);
        assertEquals("Test error", response.getDescription());
        assertEquals(500, response.getErrorCode());
    }

    @Test
    void errorResponse_shouldHaveAllArgsConstructor() {
        ErrorResponse response = new ErrorResponse("Constructor error", 401);

        assertNotNull(response);
        assertEquals("Constructor error", response.getDescription());
        assertEquals(401, response.getErrorCode());
    }

    @Test
    void errorResponse_shouldHaveEqualsAndHashCode() {
        ErrorResponse response1 = ErrorResponse.builder()
                .description("Error")
                .errorCode(400)
                .build();

        ErrorResponse response2 = ErrorResponse.builder()
                .description("Error")
                .errorCode(400)
                .build();

        ErrorResponse differentResponse = ErrorResponse.builder()
                .description("Different error")
                .errorCode(500)
                .build();

        assertEquals(response1, response2);
        assertNotEquals(response1, differentResponse);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), differentResponse.hashCode());
    }

    @Test
    void errorResponse_shouldHaveToString() {
        ErrorResponse response = ErrorResponse.builder()
                .description("Test")
                .errorCode(123)
                .build();

        String toStringResult = response.toString();

        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("Test"));
        assertTrue(toStringResult.contains("123"));
    }
}