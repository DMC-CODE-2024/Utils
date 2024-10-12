package com.eirs.pairExpiry.mapper;

import com.eirs.pairExpiry.repository.entity.ImeiPairDetailHis;
import com.eirs.pairExpiry.repository.entity.Pairing;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PairingMapper {

    ImeiPairDetailHis toImeiPairDetailHis(Pairing pairing);

}
