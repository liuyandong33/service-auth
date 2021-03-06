package build.dream.auth.mappers;

import build.dream.common.domains.saas.SystemUser;
import org.apache.ibatis.annotations.Param;

public interface SystemUserMapper {
    SystemUser findByLoginNameOrEmailOrMobile(@Param("loginName") String loginName);
}
