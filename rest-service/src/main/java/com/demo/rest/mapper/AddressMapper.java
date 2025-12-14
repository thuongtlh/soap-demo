package com.demo.rest.mapper;

import com.demo.rest.dto.AddressDto;
import com.demo.rest.generated.AddressType;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for Address conversion.
 *
 * This mapper converts between:
 * - AddressDto (REST layer) <-> AddressType (SOAP/JAXB generated)
 *
 * The @Mapper annotation with componentModel="spring" makes this a Spring bean.
 */
@Mapper(componentModel = "spring")
public interface AddressMapper {

    /**
     * Convert REST DTO to SOAP type.
     *
     * @param dto The REST AddressDto
     * @return The SOAP AddressType for the SOAP request
     */
    AddressType toSoapType(AddressDto dto);

    /**
     * Convert SOAP type to REST DTO.
     *
     * @param soapType The SOAP AddressType from SOAP response
     * @return The REST AddressDto for REST response
     */
    AddressDto toDto(AddressType soapType);
}
