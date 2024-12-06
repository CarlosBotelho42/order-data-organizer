package com.br.order_data_organizer.dto;
import java.util.List;

public record OrderDTO(
        UserDTO user,
        Long order_id,
        String total,
        String date,
        List<ProductDTO> products
){}
