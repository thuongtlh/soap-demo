package com.demo.rest.mapper;

import com.demo.rest.dto.CreateOrderRequestDto;
import com.demo.rest.dto.CreateOrderResponseDto;
import com.demo.rest.dto.GetOrderResponseDto;
import com.demo.rest.generated.CreateOrderRequest;
import com.demo.rest.generated.CreateOrderResponse;
import com.demo.rest.generated.GetOrderResponse;
import com.demo.rest.generated.OrderStatusType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * MapStruct mapper for Order conversion.
 *
 * This is the main mapper that orchestrates the conversion between:
 * - REST request DTOs <-> SOAP request types
 * - SOAP response types <-> REST response DTOs
 *
 * Demonstrates:
 * - Using other mappers (CustomerMapper, OrderItemMapper)
 * - Custom type conversions (XMLGregorianCalendar <-> LocalDateTime/LocalDate)
 * - Named mapping methods for different conversions
 * - Enum to String conversion
 */
@Mapper(componentModel = "spring", uses = {CustomerMapper.class, OrderItemMapper.class})
public interface OrderMapper {

    // ==================== REQUEST MAPPING ====================

    /**
     * Convert REST CreateOrderRequestDto to SOAP CreateOrderRequest.
     *
     * This is called when preparing the SOAP request:
     * REST Request -> MapStruct -> SOAP Request -> SOAP Service
     *
     * @param dto The REST request DTO from the client
     * @return The SOAP request to send to the SOAP service
     */
    CreateOrderRequest toSoapCreateOrderRequest(CreateOrderRequestDto dto);

    // ==================== RESPONSE MAPPING ====================

    /**
     * Convert SOAP CreateOrderResponse to REST CreateOrderResponseDto.
     *
     * This is called after receiving the SOAP response:
     * SOAP Service -> SOAP Response -> MapStruct -> REST Response
     *
     * Uses custom mapping methods for:
     * - status: OrderStatusType enum to String
     * - estimatedDeliveryDate: XMLGregorianCalendar to LocalDate
     * - createdAt: XMLGregorianCalendar to LocalDateTime
     *
     * @param soapResponse The SOAP response from the SOAP service
     * @return The REST response DTO to return to the client
     */
    @Mapping(target = "status", source = "status", qualifiedByName = "orderStatusToString")
    @Mapping(target = "estimatedDeliveryDate", source = "estimatedDeliveryDate", qualifiedByName = "xmlCalendarToLocalDate")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "xmlCalendarToLocalDateTime")
    CreateOrderResponseDto toCreateOrderResponseDto(CreateOrderResponse soapResponse);

    /**
     * Convert SOAP GetOrderResponse to REST GetOrderResponseDto.
     *
     * @param soapResponse The SOAP response from the SOAP service
     * @return The REST response DTO to return to the client
     */
    @Mapping(target = "status", source = "status", qualifiedByName = "orderStatusToString")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "xmlCalendarToLocalDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "xmlCalendarToLocalDateTime")
    GetOrderResponseDto toGetOrderResponseDto(GetOrderResponse soapResponse);

    // ==================== CUSTOM TYPE CONVERTERS ====================

    /**
     * Convert OrderStatusType enum to String.
     *
     * @param status The SOAP OrderStatusType enum
     * @return The status as a String
     */
    @Named("orderStatusToString")
    default String orderStatusToString(OrderStatusType status) {
        return status != null ? status.value() : null;
    }

    /**
     * Convert XMLGregorianCalendar to LocalDateTime.
     *
     * XMLGregorianCalendar is the standard XML date-time type used in SOAP/JAXB.
     * We convert it to Java 8 LocalDateTime for the REST layer.
     *
     * @param calendar The XML calendar from SOAP
     * @return The LocalDateTime for REST
     */
    @Named("xmlCalendarToLocalDateTime")
    default LocalDateTime xmlCalendarToLocalDateTime(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        return calendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
    }

    /**
     * Convert XMLGregorianCalendar to LocalDate.
     *
     * @param calendar The XML calendar from SOAP
     * @return The LocalDate for REST
     */
    @Named("xmlCalendarToLocalDate")
    default LocalDate xmlCalendarToLocalDate(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        return LocalDate.of(
                calendar.getYear(),
                calendar.getMonth(),
                calendar.getDay()
        );
    }
}
