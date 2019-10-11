package com.soumen.webhookdemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author Soumen Karmakar
 * 10-Oct-19
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebHook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url;
    @Column(name = "company_name")
    private String companyName;
    private String type;
}
