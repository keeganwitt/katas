package com.github.keeganwitt.katas.linkorganizer.ui.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.blackpepper.bowman.annotation.RemoteResource;
import uk.co.blackpepper.bowman.annotation.ResourceId;

import java.net.URI;

@RemoteResource("/tags")
@Data
@NoArgsConstructor
public class Tag {
    private URI id;
    private String name;
    private String description;

    @ResourceId
    public URI getId() {
        return id;
    }
}
