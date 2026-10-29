package kz.temirlan.studentapi.student;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    private static final String CONTEXT = "/alzhan-temirlan";
    private static final String PATH = CONTEXT + "/api/students";
    private static final String VALID_BODY = """
            {
                "fullName": "Temirlan Alzhan",
                "email": "temirlan@example.com",
                "groupName": "IT2-2307",
                "studentNumber": "38372"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Test
    void createReturns201WithCreatedStudent() throws Exception {
        when(studentService.create(validRequest())).thenReturn(student(7L));

        mockMvc.perform(post(PATH).contextPath(CONTEXT).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(studentJson(7L)));

        verify(studentService).create(validRequest());
    }

    @Test
    void listReturnsStudentsInServiceOrder() throws Exception {
        when(studentService.findAll()).thenReturn(List.of(student(2L), student(7L)));

        mockMvc.perform(get(PATH).contextPath(CONTEXT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].id").value(7))
                .andExpect(jsonPath("$[0].fullName").value("Temirlan Alzhan"))
                .andExpect(jsonPath("$[0].email").value("temirlan@example.com"))
                .andExpect(jsonPath("$[0].groupName").value("IT2-2307"))
                .andExpect(jsonPath("$[0].studentNumber").value("38372"));
    }

    @Test
    void listReturnsAnEmptyArrayWhenThereAreNoStudents() throws Exception {
        when(studentService.findAll()).thenReturn(List.of());

        mockMvc.perform(get(PATH).contextPath(CONTEXT))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void readReturnsStudent() throws Exception {
        when(studentService.findById(7L)).thenReturn(student(7L));

        mockMvc.perform(get(PATH + "/7").contextPath(CONTEXT))
                .andExpect(status().isOk())
                .andExpect(content().json(studentJson(7L)));

        verify(studentService).findById(7L);
    }

    @Test
    void updateReturnsUpdatedStudentAndPreservesTextStudentNumber() throws Exception {
        StudentRequest update = new StudentRequest("Temirlan Alzhan Updated", "updated@example.com",
                "IT2-2308", "0038372");
        when(studentService.update(7L, update)).thenReturn(new StudentResponse(7L, update.fullName(),
                update.email(), update.groupName(), update.studentNumber()));
        String body = """
                {
                    "fullName": "Temirlan Alzhan Updated",
                    "email": "updated@example.com",
                    "groupName": "IT2-2308",
                    "studentNumber": "0038372"
                }
                """;

        mockMvc.perform(put(PATH + "/7").contextPath(CONTEXT).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.fullName").value(update.fullName()))
                .andExpect(jsonPath("$.email").value(update.email()))
                .andExpect(jsonPath("$.groupName").value(update.groupName()))
                .andExpect(jsonPath("$.studentNumber").value("0038372"));

        verify(studentService).update(7L, update);
    }

    @Test
    void deleteReturns204WithoutABody() throws Exception {
        mockMvc.perform(delete(PATH + "/7").contextPath(CONTEXT))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(studentService).delete(7L);
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "PUT", "DELETE"})
    void missingStudentReturnsConsistent404(String method) throws Exception {
        StudentNotFoundException missing = new StudentNotFoundException(99L);
        switch (method) {
            case "GET" -> when(studentService.findById(99L)).thenThrow(missing);
            case "PUT" -> when(studentService.update(99L, validRequest())).thenThrow(missing);
            case "DELETE" -> doThrow(missing).when(studentService).delete(99L);
            default -> throw new IllegalArgumentException(method);
        }
        MockHttpServletRequestBuilder builder = request(HttpMethod.valueOf(method), PATH + "/99")
                .contextPath(CONTEXT);
        if (method.equals("PUT")) {
            builder.contentType(MediaType.APPLICATION_JSON).content(VALID_BODY);
        }

        expectError(mockMvc.perform(builder), 404, "Student with id 99 was not found", PATH + "/99");
    }

    @ParameterizedTest
    @MethodSource("invalidBodies")
    void createRejectsInvalidInputBeforeCallingService(String body) throws Exception {
        expectError(mockMvc.perform(post(PATH).contextPath(CONTEXT)
                .contentType(MediaType.APPLICATION_JSON).content(body)), 400, "Bad Request", PATH);

        verifyNoInteractions(studentService);
    }

    @ParameterizedTest
    @MethodSource("invalidBodies")
    void updateRejectsInvalidInputBeforeCallingService(String body) throws Exception {
        expectError(mockMvc.perform(put(PATH + "/7").contextPath(CONTEXT)
                .contentType(MediaType.APPLICATION_JSON).content(body)), 400, "Bad Request", PATH + "/7");

        verifyNoInteractions(studentService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"POST", "PUT"})
    void malformedJsonReturns400(String method) throws Exception {
        String requestPath = method.equals("POST") ? PATH : PATH + "/7";

        expectError(mockMvc.perform(request(HttpMethod.valueOf(method), requestPath).contextPath(CONTEXT)
                .contentType(MediaType.APPLICATION_JSON).content("{")), 400, "Bad Request", requestPath);

        verifyNoInteractions(studentService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "PUT", "DELETE"})
    void nonnumericIdReturns400(String method) throws Exception {
        expectError(mockMvc.perform(request(HttpMethod.valueOf(method), PATH + "/invalid").contextPath(CONTEXT)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_BODY)),
                400, "Bad Request", PATH + "/invalid");

        verifyNoInteractions(studentService);
    }

    @Test
    void unexpectedFailureReturns500WithoutInternalDetails() throws Exception {
        when(studentService.findAll()).thenThrow(new IllegalStateException("private database details"));

        expectError(mockMvc.perform(get(PATH).contextPath(CONTEXT)), 500, "Internal Server Error", PATH);
    }

    private static Stream<String> invalidBodies() {
        return Stream.of(
                VALID_BODY.replace("Temirlan Alzhan", " "),
                VALID_BODY.replace("temirlan@example.com", " "),
                VALID_BODY.replace("IT2-2307", " "),
                VALID_BODY.replace("38372", " "),
                VALID_BODY.replace("temirlan@example.com", "invalid-email"),
                VALID_BODY.replace("\"Temirlan Alzhan\"", "null"),
                VALID_BODY.replace("\"temirlan@example.com\"", "null"),
                VALID_BODY.replace("\"IT2-2307\"", "null"),
                VALID_BODY.replace("\"38372\"", "null"),
                "{}",
                "null");
    }

    private void expectError(ResultActions result, int expectedStatus, String message, String requestPath)
            throws Exception {
        result.andExpect(status().is(expectedStatus))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.path").value(requestPath));
    }

    private StudentRequest validRequest() {
        return new StudentRequest("Temirlan Alzhan", "temirlan@example.com", "IT2-2307", "38372");
    }

    private StudentResponse student(Long id) {
        return new StudentResponse(id, "Temirlan Alzhan", "temirlan@example.com", "IT2-2307", "38372");
    }

    private String studentJson(Long id) {
        return """
                {
                    "id": %d,
                    "fullName": "Temirlan Alzhan",
                    "email": "temirlan@example.com",
                    "groupName": "IT2-2307",
                    "studentNumber": "38372"
                }
                """.formatted(id);
    }
}
