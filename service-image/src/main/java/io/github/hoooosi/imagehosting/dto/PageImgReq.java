package io.github.hoooosi.imagehosting.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageImgReq extends PageReq {
    private String keyword;
    private List<String> tags;

    public QueryImageVOParams toQueryParams() {
        return new QueryImageVOParams()
                .setKeyword(keyword)
                .setAsc(isAsc())
                .setTags(tags);
    }
}