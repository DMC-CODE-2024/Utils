package com.eirs.repository;

import com.eirs.constants.NotificationLanguage;
import com.eirs.repository.entity.SmsConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsConfigurationEntityRepository extends JpaRepository<SmsConfigurationEntity, Long> {
    SmsConfigurationEntity findByTagAndLanguageAndModule(String tag, NotificationLanguage language, String module);
}
