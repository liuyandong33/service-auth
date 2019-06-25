package build.dream.auth.mappers;

import build.dream.common.saas.domains.SystemUser;
import org.apache.ibatis.annotations.Param;

public interface SystemUserMapper {
    SystemUser findByLoginNameOrEmailOrMobile(@Param("loginName") String loginName);
}
