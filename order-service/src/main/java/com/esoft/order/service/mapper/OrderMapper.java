package com.esoft.order.service.mapper;

import com.esoft.order.service.dto.OrderDTO;
import com.esoft.order.service.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order dtoToEntity(OrderDTO orderDTO);

    OrderDTO entityToDTO(Order order);

//    Page<OrderDTO> entitiesToDTOs(Page<Order> orders);

    @Mapping(target = "code", source = "code", ignore = true)
    Order updateEntityFromDTO(OrderDTO orderDTO, @MappingTarget Order order);
}
