package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.Duplicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DuplicateRepository extends JpaRepository<Duplicate, Long> {

    List<Duplicate> findByMsisdnIsNull();

}
