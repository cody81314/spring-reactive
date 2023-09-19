package com.example.demo.dto;

import com.example.demo.po.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDTO {
    private String id;
    private String name;
    private int age;

    public static PersonDTO createBy(Person po) {
        return PersonDTO.builder()
                .id(po.getId())
                .name(po.getName())
                .age(po.getAge())
                .build();
    }
}
