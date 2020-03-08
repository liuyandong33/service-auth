package build.dream.auth.mappers;

import build.dream.common.domains.saas.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PrivilegeMapper {
    List<AppPrivilege> obtainUserAppPrivileges(@Param("userId") Long userId);

    List<PosPrivilege> obtainUserPosPrivileges(@Param("userId") Long userId);

    List<BackgroundPrivilege> obtainUserBackgroundPrivileges(@Param("userId") Long userId);

    List<OpPrivilege> obtainUserOpPrivileges(@Param("userId") Long userId);

    List<DevOpsPrivilege> obtainUserDevOpsPrivileges(@Param("userId") Long userId);
}
