package build.dream.auth.mappers;

import build.dream.common.domains.saas.AppPrivilege;
import build.dream.common.domains.saas.BackgroundPrivilege;
import build.dream.common.domains.saas.PosPrivilege;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

public interface PrivilegeMapper {
    List<AppPrivilege> obtainUserAppPrivileges(@Param("userId") BigInteger userId);

    List<PosPrivilege> obtainUserPosPrivileges(@Param("userId") BigInteger userId);

    List<BackgroundPrivilege> obtainUserBackgroundPrivileges(@Param("userId") BigInteger userId);
}
