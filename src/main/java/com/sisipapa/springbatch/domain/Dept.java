package com.sisipapa.springbatch.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Setter
@Getter
@ToString
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
public class Dept {

    @Id
    private Integer deptNo;
    private String dName;
    private String loc;
}
