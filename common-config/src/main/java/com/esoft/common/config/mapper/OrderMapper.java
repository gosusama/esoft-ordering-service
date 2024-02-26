package com.esoft.common.config.mapper;

import com.esoft.common.config.dto.OrderDTO;
import com.esoft.common.config.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order dtoToEntity(OrderDTO orderDTO);

    OrderDTO entityToDTO(Order order);

    List<OrderDTO> entitiesToDTOs(List<Order> orders);

    @Mapping(target = "code", source = "code", ignore = true)
    Order updateEntityFromDTO(OrderDTO orderDTO, @MappingTarget Order order);
}
