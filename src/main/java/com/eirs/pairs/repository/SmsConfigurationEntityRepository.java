package com.eirs.pairs.repository;

import com.eirs.pairs.constants.NotificationLanguage;
import com.eirs.pairs.constants.SmsTag;
import com.eirs.pairs.repository.entity.SmsConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsConfigurationEntityRepository extends JpaRepository<SmsConfigurationEntity, Long> {
    SmsConfigurationEntity findByTagAndLanguageAndModule(String tag, NotificationLanguage language, String module);
}
