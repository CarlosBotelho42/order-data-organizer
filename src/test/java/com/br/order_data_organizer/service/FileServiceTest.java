package com.br.order_data_organizer.service;

import com.br.order_data_organizer.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private FileService fileService;

    @Test
    void shouldProcessDataFileSuccessfully() throws IOException, IOException {
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        String fileLine = "0000000042                               Carlos Botelho00000007120000000003     1167.1320241208";
        InputStream inputStream = new ByteArrayInputStream(fileLine.getBytes(StandardCharsets.UTF_8));
        Mockito.when(mockFile.getInputStream()).thenReturn(inputStream);

        fileService.processDataFile(mockFile);

        Mockito.verify(orderService).saveOrders(Mockito.anyList());
    }

    @Test
    void shouldRuntimeExceptionForInvalidFile() throws IOException {
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockFile.getInputStream()).thenThrow(new IOException("Invalid file"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fileService.processDataFile(mockFile);
        });

        assertEquals("Erro ao processar o arquivo... =(", exception.getMessage());
    }

    @Test
    void shouldNormalizeOrderLines() throws IOException {
        String fileLines = """
                0000000042                               Carlos Botelho00000007120000000003     1167.1320241208
                0000000043                                 Joel Botelho00000007130000000004     2168.1320241209
                0000000044                                Jessya Mendes00000007140000000005     3167.1320241210
                """;
        BufferedReader reader = new BufferedReader(new StringReader(fileLines));

        List<Order> orders = fileService.normalizeOrderLines(reader);

        assertEquals(3, orders.size());
        assertEquals("42", orders.getFirst().getUserId().toString());
        assertEquals("43", orders.get(1).getUserId().toString());
        assertEquals("44", orders.get(2).getUserId().toString());

        assertEquals("Carlos Botelho" , orders.getFirst().getUserName());
        assertEquals("Joel Botelho" , orders.get(1).getUserName());
        assertEquals("Jessya Mendes" , orders.get(2).getUserName());

        assertEquals("712", orders.getFirst().getOrderId().toString());
        assertEquals("713", orders.get(1).getOrderId().toString());
        assertEquals("714", orders.get(2).getOrderId().toString());

        assertEquals("3", orders.getFirst().getProductId().toString());
        assertEquals("4", orders.get(1).getProductId().toString());
        assertEquals("5", orders.get(2).getProductId().toString());

        assertEquals("1167.13", orders.getFirst().getProductValue().toString());
        assertEquals("2168.13", orders.get(1).getProductValue().toString());
        assertEquals("3167.13", orders.get(2).getProductValue().toString());

        assertEquals("2024-12-08", orders.getFirst().getDate().toString());
        assertEquals("2024-12-09", orders.get(1).getDate().toString());
        assertEquals("2024-12-10", orders.get(2).getDate().toString());
    }

    @Test
    void shouldThrowExceptionForInvalidLine() {
        String invalidLine = "666";

        assertThrows(IllegalArgumentException.class, () -> {
            fileService.setOrderLines(invalidLine);
        });
    }
}