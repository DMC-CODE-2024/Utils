package com.eirs.repository.entity;

import com.eirs.constants.NotificationLanguage;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "eirs_response_param", catalog = "app")
public class SmsConfigurationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag")
    private String tag;

    @Column(name = "value")
    private String msg;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private NotificationLanguage language;

    @Column(name = "feature_name")
    public String module;
}
