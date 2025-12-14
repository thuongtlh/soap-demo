package com.demo.rest.mapper;

import com.demo.rest.dto.CustomerDto;
import com.demo.rest.generated.CustomerType;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for Customer conversion.
 *
 * This mapper converts between:
 * - CustomerDto (REST layer) <-> CustomerType (SOAP/JAXB generated)
 *
 * Uses AddressMapper for nested address conversion.
 */
@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface CustomerMapper {

    /**
     * Convert REST DTO to SOAP type.
     *
     * MapStruct will automatically use AddressMapper for nested address fields:
     * - shippingAddress
     * - billingAddress
     *
     * @param dto The REST CustomerDto
     * @return The SOAP CustomerType for the SOAP request
     */
    CustomerType toSoapType(CustomerDto dto);

    /**
     * Convert SOAP type to REST DTO.
     *
     * @param soapType The SOAP CustomerType from SOAP response
     * @return The REST CustomerDto for REST response
     */
    CustomerDto toDto(CustomerType soapType);
}
