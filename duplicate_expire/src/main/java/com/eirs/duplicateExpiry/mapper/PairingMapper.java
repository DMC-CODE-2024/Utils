package com.eirs.duplicateExpiry.mapper;

import com.eirs.duplicateExpiry.repository.entity.ImeiPairDetailHis;
import com.eirs.duplicateExpiry.repository.entity.Pairing;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PairingMapper {

    ImeiPairDetailHis toImeiPairDetailHis(Pairing pairing);

}
