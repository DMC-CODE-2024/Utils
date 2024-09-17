package com.eirs.pairs.mapper;

import com.eirs.pairs.repository.entity.ImeiPairDetailHis;
import com.eirs.pairs.repository.entity.Pairing;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PairingMapper {

    ImeiPairDetailHis toImeiPairDetailHis(Pairing pairing);

}
