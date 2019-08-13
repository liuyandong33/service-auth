package build.dream.auth.mappers;

import build.dream.common.domains.saas.PosPrivilege;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

public interface PrivilegeMapper {
    List<PosPrivilege> obtainUserPosPrivileges(@Param("userId") BigInteger userId);
}
