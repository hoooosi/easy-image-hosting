package io.github.hoooosi.imagehosting.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageImgReq extends PageReq {
    private String name;
    private String introduction;
    private List<String> tags;

    public QueryImageVOParams toQueryParams() {
        return new QueryImageVOParams()
                .setName(name)
                .setIntroduction(introduction)
                .setTags(tags);
    }
}