package com.demo.rest.mapper;

import com.demo.rest.dto.OrderItemDto;
import com.demo.rest.generated.OrderItemType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * MapStruct mapper for Order Item conversion.
 *
 * This mapper converts between:
 * - OrderItemDto (REST layer) <-> OrderItemType (SOAP/JAXB generated)
 *
 * Demonstrates:
 * - Basic field mapping (same names)
 * - Custom mapping method (calculateTotalPrice)
 * - List conversion (toSoapTypeList, toDtoList)
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    /**
     * Convert REST DTO to SOAP type.
     * Uses custom expression to calculate totalPrice if not provided.
     *
     * @param dto The REST OrderItemDto
     * @return The SOAP OrderItemType for the SOAP request
     */
    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(dto))")
    OrderItemType toSoapType(OrderItemDto dto);

    /**
     * Convert SOAP type to REST DTO.
     *
     * @param soapType The SOAP OrderItemType from SOAP response
     * @return The REST OrderItemDto for REST response
     */
    OrderItemDto toDto(OrderItemType soapType);

    /**
     * Convert list of REST DTOs to list of SOAP types.
     *
     * @param dtos List of REST OrderItemDto
     * @return List of SOAP OrderItemType
     */
    List<OrderItemType> toSoapTypeList(List<OrderItemDto> dtos);

    /**
     * Convert list of SOAP types to list of REST DTOs.
     *
     * @param soapTypes List of SOAP OrderItemType
     * @return List of REST OrderItemDto
     */
    List<OrderItemDto> toDtoList(List<OrderItemType> soapTypes);

    /**
     * Calculate total price for an order item.
     * This is a default method used in the mapping expression.
     *
     * @param dto The OrderItemDto
     * @return The calculated total price (quantity * unitPrice)
     */
    default BigDecimal calculateTotalPrice(OrderItemDto dto) {
        if (dto.getTotalPrice() != null) {
            return dto.getTotalPrice();
        }
        if (dto.getQuantity() != null && dto.getUnitPrice() != null) {
            return dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
        }
        return BigDecimal.ZERO;
    }
}
