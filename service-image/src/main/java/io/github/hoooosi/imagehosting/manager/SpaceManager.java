package io.github.hoooosi.imagehosting.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.hoooosi.imagehosting.entity.Space;
import io.github.hoooosi.imagehosting.mapper.SpaceBaseMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SpaceManager {

    private final SpaceBaseMapper spaceBaseMapper;

    public int adjustingUsedCapacity(Long spaceId, long size) {
        // Check space capacity
        Space space = spaceBaseMapper.selectOne(Wrappers.lambdaQuery(Space.class)
                .select(Space::getTotalSize, Space::getMaxSize)
                .eq(Space::getId, spaceId));

        // Check and update space capacity
        return spaceBaseMapper.update(Wrappers
                .lambdaUpdate(Space.class)
                .setSql("total_size = total_size + {0}", size)
                .eq(Space::getId, spaceId)
                .le(Space::getTotalSize, space.getMaxSize() - size));
    }
}
