package com.github.keeganwitt.katas.linkorganizer.ui.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.blackpepper.bowman.annotation.LinkedResource;
import uk.co.blackpepper.bowman.annotation.RemoteResource;
import uk.co.blackpepper.bowman.annotation.ResourceId;

import java.net.URI;
import java.util.Set;

@RemoteResource("/links")
@Data
@NoArgsConstructor
public class Link {
    private URI id;
    private String url;
    private Set<Tag> tags;

    @ResourceId
    public URI getId() {
        return id;
    }

    @LinkedResource
    public Set<Tag> getTags() {
        return tags;
    }
}
